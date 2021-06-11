
# Author: Md. Shamsuzzoha Bayzid
#use warnings;
use Getopt::Long;



use feature 'say';


sub badInput {
  my $message = "Usage: perl $0 finds the sum of the weights of the satisfied quartets by a species tree
	-sp=<species tree to be scored>
	-qt=<quartet file>
	-o=<output file: not mandatory>
	-d=<division factor; the number that will be used as a denominator to find the weight: not mandatory>
	-norm=<normalized_flag; not mandatory>";
  print STDERR $message;
  die "\n";
}

GetOptions(
	"sp=s"=>\my $spFile,
	"qt=s"=>\my $qtFile,
	"o=s"=>\my $outFile,
	"d=s"=>\my $divide,
	"norm=s"=>\my $normalized_flag,
);

badInput() if not defined $spFile;
badInput() if not defined $qtFile;     

my $total_weights = 0; # Edited by 22

	# first reading the weighted quartets into an array
my %count = (); 
	open my $fh, '<', $qtFile or die "Could not open '$qtFile' $!";
	while (my $line = <$fh>) {
	    chomp $line;
		if($line =~ /^(.*) (.*)$/) {
   			$count{$1} = $2;  #Count of quartet of $1 config is = $2
   			$total_weights = $total_weights + $2 ; # Summing total_weights
	  	} # end if
			 
	}

# now finding the quartets of the species tree and summing up their corresponding weights.

my @quartets = `sh quartet_count.sh $spFile`;  # you need this shell script in your working directory

my $score = 0;
my $n_present_quartet = 0;

foreach my $q (@quartets)
{
	chomp $q;
	$score = $score + $count{$q};		
	if ($count{$q} != 0) {$n_present_quartet++};
}
   
# now output the score in stdin

if (defined $divide){$score = $score/$divide;}
		
# Always divide
my $normalized_score = $score/$total_weights;

# say "Score = $score";

# say "Total weights = $total_weights";

# say "Normalized score = $normalized_score";


if(defined $normalized_flag)
{
	if($normalized_flag == 2){
		print "$score $total_weights $normalized_score";		
	}elsif($normalized_flag == 1){
		print "$score $normalized_score";
	}
	else{
		print "$normalized_score";
	}
	
}else{
	print "$score";	
}
print "\n";

if (defined $outFile){
	open(OUT, ">", $outFile) or die "can't open $outFile: $!";
	print OUT "$n_present_quartet quartets from $qtFile is present in $spFile";
	print OUT "\nTotal score: $score\n";
	print OUT "Normalized score: $normalized_score\n";
	if (defined $divide){print OUT "\ndivided by: $divide";}
}

