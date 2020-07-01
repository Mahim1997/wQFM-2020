# Let the code begin...
package Bio::Expression::FeatureSet::FeatureSetMas50;

# $Id: FeatureSetMas50.pm,v 1.2.6.1 2006/10/02 23:10:18 sendu Exp $

=head1 NAME

Bio::Expression::FeatureSet::FeatureSetMas50

=cut

use strict;

use base qw(Bio::Expression::FeatureSet);
use vars qw($DEBUG);

use Class::MakeMethods::Emulator::MethodMaker
  get_set => [qw(
  
  probe_set_name stat_pairs stat_pairs_used
  signal detection detection_p-value
  stat_common_pairs signal_log_ratio
  signal_log_ratio_low
  signal_log_ratio_high change change_p-value
  positive negative pairs pairs_used
  pairs_inavg pos_fraction log_avg
  pos_neg avg_diff abs_call inc dec
  inc_ratio dec_ratio pos_change
  neg_change inc_dec dpos-dneg_ratio
  log_avg_ratio_change diff_call
  avg_diff_change b_a fold_change
  sort_score		 

  )],
;

1;
