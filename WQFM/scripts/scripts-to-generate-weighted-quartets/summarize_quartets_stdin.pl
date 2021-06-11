#Author: Md. Shamsuzzoha Bayzid
#use strict;
use warnings;
use Getopt::Long;

=st
sub badInput {
  my $message = "Usage: perl $0
	-i=<input file: containing a list of file names (one in each line). these files contain quartets to be summarized>
	-o=<output file>";
  print STDERR $message;
  die "\n";
}

GetOptions(
	"i=s"=>\my $inFile,
	"o=s"=>\my $outFile,
);
=cut
     
    my %count;

    #my @userinput = <STDIN>;
    while (my $line = <STDIN>) {
    #foreach $line(@userinput)
	#{
		 chomp $line;
		$count{$line}++;
	}

 foreach my $line (sort keys %count) {
    print "$line $count{$line}";
    print "\n";
	}
=st	
    my $file = shift or die "Usage: $0 FILE\n";  # this file contains the list of files (full path, one in each line) containing quartets to be summarized
    open my $fh, '<', $inFile or die "Could not open '$inFile' $!";
    while (my $line = <$fh>) {
	    chomp $line;
	    open my $fh1, '<', $line or die "Could not open '$line' $!";	
	    #foreach my $str (split /\s+/, $line) {
	    	while (my $line1 = <$fh1>) {
		    chomp $line1;
    		    $count{$line1}++;
              }
    }
     
    open(OUT, ">", $outFile) or die "can't open $outFile: $!";
    foreach my $line (sort keys %count) {
    print OUT "$line $count{$line}";
    print OUT "\n";
    }
=cut
