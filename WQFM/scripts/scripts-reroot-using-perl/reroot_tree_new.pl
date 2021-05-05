#!/usr/bin/perl

#use lib "/projects/sate3/tools/bin/bioPerl-1.5.2/lib/perl5";
use lib "bioPerl-1.5.2/lib/perl5" ;
#use lib "/home/gene-tree/Gene-Tree/tools/bioPerl-1.5.2/lib/perl5";
use Bio::TreeIO;
use strict;
use warnings;
use Getopt::Long;

sub badInput {
  my $message = "Usage: perl $0
	-t=<tree file>
	-r=<outgroup name>
	-o=<output file>";
  print STDERR $message;
  die "\n";
}

GetOptions(
	"t=s"=>\my $treefile,
	"r=s"=>\my $outgroup,
	"o=s"=>\my $outfile,
);

badInput() if not defined $treefile;
badInput() if not defined $outgroup;
badInput() if not defined $outfile;

my $treeIO = Bio::TreeIO->new(-file => $treefile,
			      -format => "newick");
my $treeIOout = Bio::TreeIO->new(-file => ">$outfile",
				 -format => "newick");

#my $tree = $treeIO->next_tree();
while( my $tree = $treeIO->next_tree) {
	foreach my $leaf ($tree->get_leaf_nodes()) {
  		if($leaf->id() eq $outgroup) {
		    $tree->reroot($leaf);
		    $treeIOout->write_tree($tree);	
    			last;
  		}
	}
}
#$treeIOout->write_tree($tree);

print "output at $outfile\n";
print "done.\n";