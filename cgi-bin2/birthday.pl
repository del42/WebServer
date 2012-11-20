#!C:/strawberry/perl/bin/perl.exe
$size_of_form_info =  $ENV{'CONTENT_LENGTH'}; 
read(STDIN, $form_info, $size_of_form_info);
$form_info =~ s/%([\dA-Fa-f][\dA-Fa-f])/pack("C", hex($1))/eg;
#the above turns %2F into a slash
#s is substitute, \dA-Fa-f looks for hex number and stores it in $1
#pack and hex convert the value in $1 to ASCII, e evaluates second part 
#of the substitute command as an expression, g replaces all occurrences
($field_name, $birthday) = split(/=/, $form_info);
print "Content-type: text/plain \n\n";
print "Your birthday is on: $birthday, right? \n"
exit(0); 
