param($Work)

# restart PowerShell with -noexit, the same script, and 1
if (!$Work) {
    powershell -noexit -file $MyInvocation.MyCommand.Path 1
    return
}

# now the script does something
# this script just outputs this:
conveyor make copied-site --overwrite