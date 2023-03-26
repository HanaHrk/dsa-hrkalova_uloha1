# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure("2") do |config|
  # The most common configuration options are documented and commented below.
  # For a complete reference, please see the online documentation at
  # https://docs.vagrantup.com.
VAGRANTFILE_API_VERSION = "2"
# set docker as the default provider
ENV['VAGRANT_DEFAULT_PROVIDER'] = 'docker'
# disable parallellism so that the containers come up in order
ENV['VAGRANT_NO_PARALLEL'] = "1"
ENV['FORWARD_DOCKER_PORTS'] = "1"
# minor hack enabling to run the image and configuration trigger just once
ENV['VAGRANT_EXPERIMENTAL']="typed_triggers"

unless Vagrant.has_plugin?("vagrant-docker-compose")
  system("vagrant plugin install vagrant-docker-compose")
  puts "Dependencies installed, please try the command again."
  exit
end

BACKEND_IMAGE  = "ds/demo-3/backend:0.1"


BACKENDS  = { :nameprefix => "backend-",  # backend nodes get names: backend-1, backend-2, etc.
              :subnet => "10.0.1.",
              :ip_offset => 100,  # backend nodes get IP addresses: 10.0.1.101, .102, .103, etc
              :image => BACKEND_IMAGE,
              :port => 5000 }
# Number of backends to start:
BACKENDS_COUNT = 1


  # Before the 'vagrant up' command is started, build docker images:
  config.trigger.before :up, type: :command do |trigger|
    trigger.name = "Build docker images and configuration files"
    trigger.ruby do |env, machine|
      # --- start of Ruby script ---
      # Build load balancer configuration file:
      puts "Building frontend LB configuration."
      #File.delete(FRONTEND[:lb_config_file]) if File.exist?(FRONTEND[:lb_config_file])
      #cfile = File.new(FRONTEND[:lb_config_file], "w")
      #cfile.puts "upstream #{FRONTEND[:lb_name]} {"
      # (1..BACKENDS_COUNT).each do |i|
      #  node_name = "#{BACKENDS[:nameprefix]}#{i}"
      #  cfile.puts "    server #{node_name}:#{BACKENDS[:port]} weight=1;"
      # end
      #cfile.puts "}"
      #cfile.close
      # Build image for backend nodes:
      puts "Building backend node image:"
      `docker build backend -t "#{BACKEND_IMAGE}"`
      # Build image for the frontend node:
      # puts "Building frontend node image:"
      # `docker build frontend -t "#{FRONTEND_IMAGE}"`
      # --- end of Ruby script ---
    end
  end

  config.vm.synced_folder ".", "/vagrant", type: "rsync", rsync__exclude: ".*/"
  config.ssh.insert_key = false

  # Definition of N backends
  (1..BACKENDS_COUNT).each do |i|
    node_ip_addr = "#{BACKENDS[:subnet]}#{BACKENDS[:ip_offset] + i}"
    node_name = "#{BACKENDS[:nameprefix]}#{i}"
    # Definition of BACKEND
    config.vm.define node_name do |s|
      s.vm.network "private_network", ip: node_ip_addr
      s.vm.hostname = node_name
      s.vm.provider "docker" do |d|
        d.image = BACKENDS[:image]
        d.name = node_name
        d.has_ssh = true
      end
      s.vm.post_up_message = "Node #{node_name} up and running. You can access the node with 'vagrant ssh #{node_name}'}"
    end
  end
  # Disable automatic box update checking. If you disable this, then
  # boxes will only be checked for updates when the user runs
  # `vagrant box outdated`. This is not recommended.
  # config.vm.box_check_update = false

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8080" will access port 80 on the guest machine.
  # NOTE: This will enable public access to the opened port
  # config.vm.network "forwarded_port", guest: 80, host: 8080

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine and only allow access
  # via 127.0.0.1 to disable public access
  # config.vm.network "forwarded_port", guest: 80, host: 8080, host_ip: "127.0.0.1"

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  # config.vm.network "private_network", ip: "192.168.33.10"

  # Create a public network, which generally matched to bridged network.
  # Bridged networks make the machine appear as another physical device on
  # your network.
  # config.vm.network "public_network"

  # Share an additional folder to the guest VM. The first argument is
  # the path on the host to the actual folder. The second argument is
  # the path on the guest to mount the folder. And the optional third
  # argument is a set of non-required options.
  # config.vm.synced_folder "../data", "/vagrant_data"

  # Provider-specific configuration so you can fine-tune various
  # backing providers for Vagrant. These expose provider-specific options.
  # Example for VirtualBox:
  #
  # config.vm.provider "virtualbox" do |vb|
  #   # Display the VirtualBox GUI when booting the machine
  #   vb.gui = true
  #
  #   # Customize the amount of memory on the VM:
  #   vb.memory = "1024"
  # end
  #
  # View the documentation for the provider you are using for more
  # information on available options.

  # Enable provisioning with a shell script. Additional provisioners such as
  # Ansible, Chef, Docker, Puppet and Salt are also available. Please see the
  # documentation for more information about their specific syntax and use.
  # config.vm.provision "shell", inline: <<-SHELL
  #   apt-get update
  #   apt-get install -y apache2
  # SHELL
end
