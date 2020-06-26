# def create_bins(lower_bound, upper_bound, num_values):

def create_bins(lower_bound=0.5, upper_bound=1.0, step_size=0.05):
    bins = []
    val = lower_bound
    while val < upper_bound:
        val_lower_3dp = '%.3f'%(val)
        val += step_size
        val_higher_3dp = '%.3f'%(val)
        _bin = (val_lower_3dp, val_higher_3dp)
        bins.append(_bin)

    return bins

bins = create_bins(0.5, 1.0, step_size=0.05)
print(bins)

print("")