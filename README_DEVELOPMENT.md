### Sandbox Development and Testing

Here is some guidance for developing on this code in a sandboxed
virtualized environment. It requires VirtualBox and Vagrant, probably
some other things.

Get our Jenkins Vagrant project.

```bash
$ git clone https://github.com/EuPathDB/vagrant-jenkins.git

$ cd vagrant-jenkins
```

If you want a specific version of Jenkins, edit
`puppet/environments/production/hieradata/common.yaml`.

```yaml
ebrc_jenkins::instances:
  CI:
  version: 1.658
```

Start the VM, Vagrant will provision a Jenkins instance. Downloading the
Jenkins war file during provisioning can be very slow. Go get some lunch.

```bash
$ vagrant up
```

Checkout jenkins-website-job-generator code into the `scratch` directory
of the Vagrant project.

```bash
$ cd scratch
$ git clone git@github.com:EuPathDB/jenkins-website-job-generator.git
```

Puppet isn't reloading the firewall after opening Jenkins port, so do it
manually.

```bash
$ vcssh master
$ sudo systemctl restart firewalld
```

Jenkins will be available at
[http://ci.jenkins.vm:9181](http://ci.jenkins.vm:9181)

Install plugins

- Build Timeout
- Job DSL
- Email Extension Plugin
- Email Extension Template Plugin
- Jabber (XMPP) notifier and control plugin
- Update Subversion Plug-in (if needed)

A quick way to install (esp. if current plugins are too new for the version of Jenkins)
is to rsync from production.

```
[root@ci ~]# rsync -av -e 'ssh -p2112' <user>@<prod_jenkins_host>:/usr/local/home/jenkins/Instances/CI/plugins/ ~jenkins/Instances/CI/plugins/
[root@ci ~]# systemctl restart jenkins@CI
```

Download a copy of our extended email template.

```bash
$ rsync -a <user>@<prod_jenkins_host>:/usr/local/home/jenkins/Instances/CI/email-templates/eupath-email-ext.jelly ~jenkins/Instances/CI/email-templates/
```
 	
Create Freestyle project for `generate-website-jobs`

Use project settings:

  - Advanced Project Options
    - Use custom workspace `/vagrant/scratch/jenkins-website-job-generator/`

  - Build
    - Process Job DSLs
      - Look on Filesystem
        - DSL Scripts: generate_website_jobs

  - Save

On Vagrant box,

```
cd /vagrant/scratch/
git clone git@github.com:EuPathDB/jenkins-website-job-generator.git
```

In Jenkins UI for `generate-website-jobs` run `Build Now`.

Do your edits and commits from
`/vagrant/scratch/jenkins-website-job-generator` on the guest or
`scratch/jenkins-website-job-generator` in the Vagrant directory on the
host.


