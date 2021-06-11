#!/lusr/perl5.8/bin/perl -w

eval 'exec /lusr/perl5.8/bin/perl -w -S $0 ${1+"$@"}'
    if 0; # not running under some shell
# Author:  Jason Stajich <jason@bioperl.org>
# Purpose: Retrieve the NCBI Taxa ID for organism(s)

# TODO: add rest of POD
#

use LWP::UserAgent;
use XML::Twig;
use strict;
use Getopt::Long;
my $verbose = 0;
my $plain   = 0;
my $help    = 0;
my $USAGE = "taxid4species: [-v] [-p] \"Genus1 species1\" \"Genus2 species2\"";

GetOptions('v|verbose' => \$verbose,
	   'p|plain'   => \$plain,
	   'h|help'    => \$help);
die("$USAGE\n") if $help;

my $ua = new LWP::UserAgent();

my $urlbase = 'http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=taxonomy&term=';

my (@organisms) = @ARGV;
die("must provide valid organism") unless @organisms;
my $organismstr = join(" OR ", @organisms);
$organismstr =~ s/\s/\+/g;

my $response = $ua->get($urlbase.$organismstr);
my $t = XML::Twig->new();
print $response->content,"\n"if($verbose);
$t->parse($response->content);
my $root = $t->root;
my $list = $root->first_child('IdList');
my @data;
foreach my $child ($list->children('Id') ) {
    push @data, $child->text;
    if( $plain ) { print $child->text, "\n" }
}
unless( $plain  ) {
    $list = $root->first_child('TranslationStack');
    foreach my $set ($list->children('TermSet') ) {
	foreach my $term ( $set->children('Term') ) {
	    print "\"",$term->text(), "\", ", shift @data, "\n";
	}
    }
}

=head1 NAME

taxid4species: Simple script which returns the NCBI Taxanomic id for a requested species

=head1 DESCRIPTION

This simple script shows how to get the taxa id from NCBI Entrez and
will return a list of taxa ids for requested organisms.

=cut

