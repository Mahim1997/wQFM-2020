import sys
import dendropy

if len(sys.argv) != 3:
    print(f"USAGE: python3 {sys.argv[0]} <input-nexus-file> <output-newick-file-name>")
    exit()


tree = dendropy.Tree.get(path=sys.argv[1], schema="nexus")

# tree = dendropy.Tree.get(path="all.wQFM.bootstrap.scored", schema="nexus")

s = tree.as_string("newick").strip()
s = s.replace("[&U] ", "")
s = s + "\n"


with open(sys.argv[2], "w") as fout:
    fout.write(s)

# tree.write(path="all.wQFM.bootstrap.scored.newick", schema="newick")