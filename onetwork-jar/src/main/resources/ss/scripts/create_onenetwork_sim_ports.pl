#!/usr/bin/perl -w
# Created by  : Fatih ONUR
# Created in  : 2015.09.28
##
### VERSION HISTORY
# Ver1        : Created for OneNetwork-Simulation Service
# Purpose     : Creates simulations ports
# Description : Creates simulations ports. Modified version of simdep version.
# Date        : 2015.09.28
# Who         : Fatih ONUR
##########################################
#
#----------------------------------------------------------------------------------
#Variables
#----------------------------------------------------------------------------------
my $NETSIM_INSTALL_SHELL   = "/netsim/inst/netsim_pipe";
my $lineAddPort            = undef;
my $lineConfigPort         = undef;
my $flagDefaultDestination = undef;
my $hostName               = `hostname`;
chomp($hostName);

#
#----------------------------------------------------------------------------------
#Check if the scrip is executed as netsim user
#----------------------------------------------------------------------------------
#
$user = `whoami`;
chomp($user);
$netsim = 'netsim';
if ( $user ne $netsim ) {
    print "ERROR: Not netsim user. Please execute the script as netsim user\n";
    exit(201);
}

#
#----------------------------------------------------------------------------------
#Check if the script usage is right
#----------------------------------------------------------------------------------
$USAGE = "Usage: $0 \n  E.g. $0\n";
if ( @ARGV != 0 ) {
    print "ERROR: $USAGE";
    exit(202);
}
print "RUNNING: $0 @ARGV \n";

#
#----------------------------------------------------------------------------------
#Environment Variable
#---------------------------------------------------------------------------------
my $createDefaultDestination = "0.0.0.0";
my $flagSgsn                 = 0;
my $dummyPortAddrIpv4        = "0.0.0.1";
my $dummyPortAddrIpv6        = "2001:1b70:82a1:103::64:1";

my $createDefaultDestinationIpv6 = "2001:1b70:82a1:0103::12";

#
#----------------------------------------------------------------------------------
#Map the Port and create Port
#----------------------------------------------------------------------------------
sub buildPort {
    ( my $lineAddPort, my $lineConfigPort, my $portName ) = @_;
    print "Create Port for $portName \n";
    print MML ".select configuration\n";
    print MML "$lineAddPort\n";
    print MML "$lineConfigPort\n";
    print MML ".config save\n";
}

sub buildDD {
    (
        my $lineAddPortDd,
        my $lineConfigPortDd,
        my $createDefaultDestinationName,
        my $hostName
    ) = @_;
    print MML ".select configuration\n";

    print MML "$lineAddPortDd\n";
    print MML
      ".config external servers $createDefaultDestinationName $hostName\n";

    print MML "$lineConfigPortDd\n";
    print MML ".config save\n";
}

#----------------------------------------------------------------------------------
#Subroutine to create ports
#----------------------------------------------------------------------------------
sub createPorts {
    ( my $createPort ) = @_;
    $_ = $createPort;
    my $portName = "$createPort";

    if ( $_ =~ m/SGSN$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort = ".config add port $portName netsimwpp $hostName";
        $lineConfigPort =
          ".config port address $portName $dummyPortAddrIpv4 4001";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $portName    = "$createPort-GSN";
        $lineAddPort = ".config add port $portName snmp_ssh_prot $hostName";
        $lineConfigPort =
".config port address $portName $dummyPortAddrIpv4 1161 public 2 %unique %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName snmp_ssh_prot";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }
    elsif ( $_ =~ m/IIOP_PROT/ ) {
        $lineAddPort = ".config add port $portName iiop_prot $hostName";
        $lineConfigPort =
".config port address $portName nehttpd $dummyPortAddrIpv4 56834 56836 no_value";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );

    }
    elsif ( $_ =~ m/NETCONF_PROT$/ ) {
        my $createDefaultDestinationName = "$createPort"; 
        $lineAddPort = ".config add port $portName netconf_prot $hostName";
        $lineConfigPort =
".config port address $portName $dummyPortAddrIpv4 1161 public 2 %unique 1 %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName netconf_prot";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }
    elsif ( $_ =~ m/NETCONF_PROT_TLS$/ ) {
        my $createDefaultDestinationName = "$createPort"; 
        $lineAddPort = ".config add port $portName netconf_prot $hostName";
        $lineConfigPort =
".config port address $portName $dummyPortAddrIpv4 1161 public 2 %unique 2 %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName netconf_prot";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }
    elsif ( $_ =~ m/NETCONF_PROT_SSH$/ ) {
        my $createDefaultDestinationName = "$createPort"; 
        $lineAddPort = ".config add port $portName netconf_prot $hostName";
        $lineConfigPort =
".config port address $portName $dummyPortAddrIpv4 1161 public 2 %unique 3 %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName netconf_prot";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }
    elsif ( $_ =~ m/SGSN_PROT$/ ) {
        my $createDefaultDestinationName = "$createPort"; 
        $lineAddPort = ".config add port $portName sgsn_prot $hostName";
        $lineConfigPort =
".config port address $portName $dummyPortAddrIpv4 1161 public 2 %unique 4001 %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName sgsn_prot";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }
    elsif ( $_ =~ m/SNMP_SSH_PROT$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort = ".config add port $portName snmp_ssh_prot $hostName";
        $lineConfigPort =
".config port address $portName $dummyPortAddrIpv4 1161 public 1|2 %unique %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName snmp_ssh_prot";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }
    elsif ( $_ =~ m/SNMP$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort = ".config add port $portName snmp $hostName";
        $lineConfigPort =
".config port address $portName $dummyPortAddrIpv4 1161 public 1|2 %unique %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName snmp";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }
    elsif ( $_ =~ m/APG_TELNET_APGTCP$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort = ".config add port $portName apgtcp $hostName";
        $lineConfigPort =
          ".config port address $portName $dummyPortAddrIpv4 5002 5022 23";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName apgtcp";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 50000 50010";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }
    elsif ( $_ =~ m/APG_APGTCP$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort = ".config add port $portName apgtcp $hostName";
        $lineConfigPort =
          ".config port address $portName $dummyPortAddrIpv4 5000 5022 23";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName apgtcp";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 50000 50010";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }
    elsif ( $_ =~ m/APG43L_APGTCP$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort = ".config add port $portName apgtcp $hostName";
        $lineConfigPort =
          ".config port address $portName $dummyPortAddrIpv4 5000 5022 23";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName apgtcp";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 65505 65510";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }
    elsif ( $_ =~ m/MSC_S_CP$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort = ".config add port $createPort msc-s_cp_prot $hostName";
        $lineConfigPort = ".config port address force_no_value $createPort";
        &buildPort( $lineAddPort, $lineConfigPort, $createPort );
    }
    elsif ( $_ =~ m/STN_PROT$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort = ".config add port $portName snmp_ssh_prot $hostName";
        $lineConfigPort =
".config port address $portName $dummyPortAddrIpv4 1161 public 2 %unique %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName snmp_ssh_prot";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }
    elsif ( $_ =~ m/LANSWITCH_PROT$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort =
          ".config add port $createPort snmp_ssh_telnet_prot $hostName";
        $lineConfigPort =
".config port address $createPort $dummyPortAddrIpv4 1161 public 2 %unique %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $createPort );
        $lineAddPortDd =
".config add external $createDefaultDestinationName snmp_ssh_telnet_prot";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }
    elsif ( $_ =~ m/TSP_PROT$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort = ".config add port $createPort tsp_prot $hostName";
        $lineConfigPort =
".config port address $createPort $dummyPortAddrIpv4 1161 public 2 %unique 7423 %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $createPort );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName tsp_prot";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );
    }
    elsif ( $_ =~ m/TSP_SSH_PROT$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort = ".config add port $createPort tsp_ssh_prot $hostName";
        $lineConfigPort =
".config port address $createPort $dummyPortAddrIpv4 1161 public 2 %unique 7423 %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $createPort );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName tsp_ssh_prot";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );
    }
    elsif ( $_ =~ m/HTTP_HTTPS_PORT$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort = ".config add port $createPort http_https_port $hostName";
        $lineConfigPort =
".config port address $createPort $dummyPortAddrIpv4 1161 public 2 %unique %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $createPort );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName http_https_port";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestination 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }

}

#----------------------------------------------------------------------------------
#Subroutine to create ports IPV6
#----------------------------------------------------------------------------------
sub createPortsIpv6 {
    ( my $createPort ) = @_;
    $_ = $createPort;
    my $portName = "$createPort";

    if ( $_ =~ m/IIOP_PROT_IPV6/ ) {
        $lineAddPort = ".config add port $portName iiop_prot $hostName";
        $lineConfigPort =
".config port address $portName nehttpd $dummyPortAddrIpv6 56834 56836 no_value";

        &buildPort( $lineAddPort, $lineConfigPort, $portName );
    }
    elsif ( $_ =~ m/NETCONF_PROT_TLS_IPV6$/ ) {
        my $createDefaultDestinationName = "$createPort";
        $lineAddPort = ".config add port $portName netconf_prot $hostName";
        $lineConfigPort =
".config port address $portName $dummyPortAddrIpv6 1161 public 2 %unique 2 %simname_%nename authpass privpass 2 2";
        &buildPort( $lineAddPort, $lineConfigPort, $portName );
        $lineAddPortDd =
          ".config add external $createDefaultDestinationName netconf_prot";
        $lineConfigPortDd =
".config external address $createDefaultDestinationName $createDefaultDestinationIpv6 162 1";
        &buildDD( $lineAddPortDd, $lineConfigPortDd,
            $createDefaultDestinationName, $hostName );

    }

}

#----------------------------------------------------------------------------------
#Define NETSim MO file and Open file in append mode
#----------------------------------------------------------------------------------
$MML_MML = "MML.mml";
open MML, "+>>$MML_MML";

#----------------------------------------------------------------------------------
#Call to create all kinds of ipv4 ports
#----------------------------------------------------------------------------------
&createPorts("IIOP_PROT");
&createPorts("NETCONF_PROT_SSH");
#&createPorts("NETCONF_PROT");
#&createPorts("NETCONF_PROT_TLS");
#&createPorts("SGSN");
#&createPorts("STN_PROT");
#&createPorts("SGSN_PROT");
#&createPorts("SNMP_SSH_PROT");
#&createPorts("SNMP");
#&createPorts("APG_APGTCP");
#&createPorts("MSC_S_CP");
#&createPorts("LANSWITCH_PROT");
#&createPorts("APG43L_APGTCP");
#&createPorts("TSP_PROT");
#&createPorts("TSP_SSH_PROT");
#&createPorts("HTTP_HTTPS_PORT");
#&createPorts("APG_TELNET_APGTCP");
#----------------------------------------------------------------------------------
#Call to create all kinds of ipv6 ports
#----------------------------------------------------------------------------------
#&createPortsIpv6("IIOP_PROT_IPV6");
#&createPortsIpv6("NETCONF_PROT_TLS_IPV6");

#
system("$NETSIM_INSTALL_SHELL < $MML_MML");
if ($? != 0)
{
    print "ERROR: Failed to execute system command ($NETSIM_INSTALL_SHELL < $MML_MML)\n";
    exit(207);
}
close MML;
system("rm $MML_MML");
if ($? != 0)
{
    print "INFO: Failed to execute system command (rm $MML_MML)\n";
}

print "FINISHED: $0 @ARGV \n";