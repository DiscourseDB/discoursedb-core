import csv
import sys
import argparse

csv.field_size_limit(sys.maxsize)

parser = argparse.ArgumentParser()
parser.add_argument('--bigcsv', help='Big CSV file you want to split into smaller ones', required=True)
parser.add_argument('--maxlen', help='Max length of resulting split up files', default=1000, type=int)
parser.add_argument('--prefix', help="Prefix of resulting smaller files.", default="split_tmp/split_")

args, unknown = parser.parse_known_args()

fromf = csv.DictReader(open(args.bigcsv))
filecounter = 1
tof = None
for rowcounter, row in enumerate(fromf):
    if rowcounter % args.maxlen == 0:
        tof = csv.writer(open(args.prefix + ("%05d" % (filecounter,)) + ".csv", "w"))
        tof.writerow(fromf.fieldnames)
        filecounter += 1
    tof.writerow([row[f] for f in fromf.fieldnames])


