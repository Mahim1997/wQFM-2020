package Bio::DB::SeqFeature::Store::DBI::Iterator;

# $Id: Iterator.pm,v 1.1.4.1 2006/10/02 23:10:17 sendu Exp $

=head1 NAME

Bio::DB::SeqFeature::Store::DBI::Iterator

=cut

sub new {
  my $class          = shift;
  my ($sth,$store)   = @_;
  return bless {sth   => $sth,
		store => $store
	       },ref($class) || $class;
}

sub next_seq {
  my $self  = shift;
  my $sth   = $self->{sth}   or return;
  my $store = $self->{store} or return;
  my $obj   = $store->_sth2obj($sth);
  if (!$obj) {
    undef $self->{sth};
    undef $self->{store};
    return;
  }
  return $obj;
}

1;
