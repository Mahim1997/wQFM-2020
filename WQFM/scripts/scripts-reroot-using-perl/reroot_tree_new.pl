#!/lusr/bin/perl
# Point to the path to where bioPerl is stored in your directory structure.
# use lib "/home/gene-tree/Gene-Tree/tools/bioPerl-1.5.2/lib/perl5";
use lib "/home/mahim/gene-tree-tools/bioPerl-1.5.2/lib/perl5";
use Bio::Tree::Tree;
use IO::String;
use Bio::TreeIO;
Bio::TreeIO::newick;
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

print("-->>>INSIDE INSIDE INSDE .... ")
#my $treeIO = Bio::TreeIO->new(-file => $treefile,
#			      -format => "newick");
my $treeIOout = Bio::TreeIO->new(-file => ">$outfile",
				 -format => "newick");

my $string = "(A,(B,C));";
my $io = IO::String->new($treefile);
my $treeio = Bio::TreeIO->new(-fh => $io,
                              -format => 'newick');

while( my $tree = $treeio->next_tree) {

 	my $node = $tree->find_node(-id => $outgroup);  # name to actual id map korlam
	print "\n node: $node";
	#$tree->reroot($node);

	my $parent = $node->ancestor;
	my $tree_root = $tree->get_root_node;

	#print "\nroot: $tree_root";
	#print "\n pa: $parent";

	#if ($tree_root ne $parent)
	#{
		#print "not same root";
		#$tree->reroot($parent);
		$tree->reroot($node);
	#}

	$treeIOout->write_tree($tree);	
#print "Newick format: ", scalar $tree->number_nodes, "\n";

#return scalar $tree->number_nodes;
#print "nn", $tree->as_text('newick');

}
#$treeIOout->write_tree($tree);

print "output at $outfile\n";
print "done.\n";