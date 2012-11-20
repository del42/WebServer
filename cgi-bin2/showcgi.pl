#!/usr/bin/perl
# Order the lines above so that the first gives the location
# of Perl on your system.  The rest become comments. 

#simple Perl script to print CGI inputs

#require "cgi-lib.pl";

#print  &PrintHeader;
print "Content-type: text/html\n\n";
print "<HEAD>\n";
print "<TITLE>Show CGI Inputs</TITLE>\n";
print "</HEAD>";
print "<BODY>";
print "<h1>Show CGI Inputs:</h1><hr>\n";

print "<h2>Command Line Arguments:</h2>\n";

$j=1;
foreach $a (@ARGV)
{
print "arg$j: $a<BR>\n";
$j=$j+1;
}


print "<HR>";
print "<h2>Environment Variables:</h2>\n";
print "SERVER_SOFTWARE = $ENV{'SERVER_SOFTWARE'}<BR>\n";
print "SERVER_NAME = $ENV{'SERVER_NAME'}<BR>\n";
print "GATEWAY_INTERFACE = $ENV{'GATEWAY_INTERFACE'} <BR>\n";
print "SERVER_PROTOCOL = $ENV{'SERVER_PROTOCOL'}<BR>\n";
print "SERVER_PORT = $ENV{'SERVER_PORT'}<BR>\n";
print "REQUEST_METHOD = $ENV{'REQUEST_METHOD'}<BR>\n";
print "HTTP_ACCEPT = $ENV{'HTTP_ACCEPT'}<BR>\n";
print "PATH_INFO = $ENV{'PATH_INFO'}<BR>\n";
print "PATH_TRANSLATED = $ENV{'PATH_TRANSLATED'}<BR>\n";
print "SCRIPT_NAME = $ENV{'SCRIPT_NAME'}<BR>\n";
print "QUERY_STRING = $ENV{'QUERY_STRING'}<BR>\n";
print "REMOTE_HOST = $ENV{'REMOTE_HOST'}<BR>\n";
print "REMOTE_ADDR = $ENV{'REMOTE_ADDR'}<BR>\n";
print "REMOTE_USER = $ENV{'REMOTE_USER'}<BR>\n";
print "CONTENT_TYPE = $ENV{'CONTENT_TYPE'}<BR>\n";
print "CONTENT_LENGTH = $ENV{'CONTENT_LENGTH'}<BR>\n";
print "HTTP_REFERER = $ENV{'HTTP_REFERER'}<BR>\n";
print "HTTP_USER_AGENT = $ENV{'HTTP_USER_AGENT'}<BR>\n";
print "HTTP_COOKIE = $ENV{'HTTP_COOKIE'}<BR>\n";
print "<hr>\n";
print "<h2>Standard Input:</h2>\n";

#get buffer from QUERY_STRING (GET) or STDIN (POST)
if ($ENV{'REQUEST_METHOD'} eq "GET") 
{
$buffer = $ENV{'QUERY_STRING'};
print "There is no input in STDIN";
print " when using GET method.<BR>\n";
}
else 
{ 
read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});
print "$buffer\n";
}

print "<hr>";
print "<h2>Name/Value pairs extracted</h2>\n";

#check for equal signs in buffer
$e = index($buffer,"=");

if ( $e == -1 )
{
print "no name/value pairs in input\n";
}
else
{
  #make an array of pairs split at the & sign

    @nvpairs = split(/&/, $buffer);

  #for each pair, extract name and value

  foreach $pair (@nvpairs)
  {  
    #split into name and value	
        ($name, $value) = split(/=/, $pair);   
    #print name/value pair      
         print "$name = $value<BR>\n";
  }
}


print "<hr>";
print "<h2>Name/Value pairs decoded</h2>\n";

if ( $e != -1 )
{
  foreach $pair (@nvpairs)
  {  

    #convert plusses to spaces
        $pair =~ s/\+/ /g;    
    #split into name and value	
        ($name, $value) = split(/=/, $pair);   
    #decode any %XX from hex numbers to alphanumeric
        $name =~ s/%(..)/pack("c",hex($1))/ge;
        $value =~ s/%(..)/pack("c",hex($1))/ge;
    #print name/value pair and decoded value      
        print "$name = $value<BR>\n";
  }
}


print "</BODY><HTML>\n";
