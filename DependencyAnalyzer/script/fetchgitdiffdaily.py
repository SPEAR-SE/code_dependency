import git
import os
import shutil
import sys
import datetime
from fileinput import filename

# create a clean folder with name DIR_NAME
DIR_NAME = sys.argv[2]#"/Users/zipeng/Projects/gitdir/hadoop"
REMOTE_URL = "https://github.com/apache/hadoop.git"
COMMITS_TO_PRINT = 10


def new_dir():
    if os.path.isdir(DIR_NAME):
        shutil.rmtree(DIR_NAME)
        os.mkdir(DIR_NAME)
    assert os.path.isdir(DIR_NAME)


def print_commit(commit):
    print('----')
    print(str(commit.hexsha))
    print("\"{}\" by {} ({})".format(commit.summary,
                                     commit.author.name,
                                     commit.author.email))
    print(str(commit.authored_datetime))
    # print(str("count: {} and size: {}".format(commit.count(),commit.size)))


def print_repository(repo):
    print('Repo description: {}'.format(repo.description))
    # print('Repo active branch is {}'.format(repo.active_branch))
    for remote in repo.remotes:
        print('Remote named "{}" with URL "{}"'.format(remote, remote.url))
    print('Last commit for repo is {}. \n'.format(str(repo.head.commit.hexsha)))


# clone remote project to local
def git_clone():
    git.Repo.clone_from(REMOTE_URL, DIR_NAME)


def git_checkout_version(git_version):
    print("-------------------------------------------------------------------------------")
    # commit_version = input("The commit you want to checkout is:  ")
    #repo.git.checkout('trunk')
    #print("Now the head moves to: " + git_version)
    repo.git.checkout(git_version)
    print("git branch information shows as below: \n" + repo.git.branch())
    print("-------------------------------------------------------------------------------")
    print('')

def git_diff_print(parentversion, version):
    # output all the git diff to file: diff_between_commits
    filename = "diff_between_commits.txt"
    diff_between_commits = open(filename, "w")
    git_diff_info = repo.git.diff(parentversion, version)
    diff_between_commits.write("%s \n" % git_diff_info)
    diff_between_commits.close


def git_diff_pre(parentversion, version,i,diff_to_file):
    # output only changed classes' path
    #diff_to_file = open('%s_%s.txt' % (version,parentversion), "w")
    #writefilename = 'diff_to_file_'+i+'.txt'   
    changed_file = []
    diff_index = repo.commit(version).diff(parentversion, create_patch=True)   
    diff_to_file.write("version difference between: \n")  
    diff_to_file.write("first_commit:  %s \n" %  version)
    diff_to_file.write("last_commit: %s \n" % parentversion)
    '''’A’ for added paths
       ’D’ for deleted paths
       ’R’ for renamed paths
       ’M’ for paths with modified data
       ’T’ for changed in the type paths'''
    for diff_item in diff_index.iter_change_type('M' or 'A' or 'D' or 'R' or 'T'):
        # print("A blob:\n{}".format(diff_item.a_blob.data_stream.read().decode('utf-8')))
        # print("B blob:\n{}".format(diff_item.b_blob.data_stream.read().decode('utf-8')))
        if diff_item.a_blob.path not in changed_file:
            changed_file.append(diff_item.a_blob.path)
        if diff_item.b_blob is not None and diff_item.b_blob.path not in changed_file:
            changed_file.append(diff_item.b_blob.path)
    for item in changed_file:
        diff_to_file.write("%s \n" % item)       
    
   # changed_file.clear()
   
def git_show(diff_to_file,version):
    gitshow = repo.git.show(version,'--name-only')
    diff_to_file.write("%s \n" % gitshow)
    #print(gitshow)
    
def minusoneday(cdate):
    year = cdate.year
    month = cdate.month
    day = cdate.day
    if month == 1:
        if day > 1:
            day = day - 1
        else:
            day = 31
            month = 12
            year = year - 1
    elif month == 5 or month == 7 or month == 8 or month == 10 or month == 12:
        if day > 1:
            day = day -1
        else:
            day = 30;
            month = month - 1
    elif month == 3 :
            if day > 1 :
                day = day - 1
            else:
                if (year % 4) == 0:
                    if (year % 100) == 0:
                        if (year % 400) == 0:
                            month = month - 1
                            day = 29
                        else:
                            month = month -1
                            day = 28
                    else:
                        month = month - 1
                        day = 29
                else:
                    month=month-1
                    day=28
    else:
        if day > 1:
            day = day - 1
        else:
            day = 31
            month = month - 1

    newdate = datetime.datetime(year, month, day, 0,0,0)
    return newdate


def main(arg1,arg2,arg3):
    arg = int(arg1)
    
    commitlist = list(repo.iter_commits(arg3))
    #recentcommit = list(repo.iter_commits('trunk'))[0]
    tags = repo.tags
    tagref = tags[arg2]
    releasecommit = tagref.commit
     # check out the  version and print the commit information
    repo.git.checkout(releasecommit) 
    
    cyear = releasecommit.committed_datetime.year
    cmonth = releasecommit.committed_datetime.month
    cday = releasecommit.committed_datetime.day
    
    print('Last commit date:  %s' % datetime.datetime(cyear, cmonth, cday,0,0,0))
    commitdate = datetime.datetime(cyear, cmonth, cday,0,0,0)
    for i in range(0, arg):
        diff_to_file = open('diff_to_file_%s.txt' % i,'w')  
        diff_to_file.write("analyze_date: %s\n" % commitdate.date())      
        print('In date:  %s' % commitdate.date())
        commit_list = []
        for c in commitlist:
            if c.committed_datetime.date() == commitdate.date():
                commit_list.append(c)
                git_show(diff_to_file,c)
        diff_to_file.write('total_commits_in_the_day: %s \n' % len(commit_list))
        #if(len(commit_list) > 0):
            #COMMIT_VERSION = commit_list[0]
            #COMMIT_PARENT_VERSION = commit_list[len(commit_list) - 1]                   
            # compare this version with parent version
            #git_diff_pre(COMMIT_PARENT_VERSION, COMMIT_VERSION,i,diff_to_file) 
        diff_to_file.close()
            #compare this version with parent version, get all the changed methods
            #git_diff_print(COMMIT_PARENT_VERSION, COMMIT_VERSION)
           
        commitdate = minusoneday(commitdate)
        

if __name__ == "__main__":    
    print("working on existing package in path: " + DIR_NAME)

    repo_path = DIR_NAME
    # Repo object used to programmatically interact with Git repositories
    repo = git.Repo(repo_path)

    # check that the repository loaded correctly
    if not repo.bare:
        print('Repo at {} successfully loaded.'.format(repo_path))
        print_repository(repo)
        # create list of commits then print some of them to stdout
        print("Git branch information shows as below: " + "\n" + repo.git.branch() + "\n")
    else:
        print('Could not load repository at {} :('.format(repo_path))
   
    main(sys.argv[1],sys.argv[3],sys.argv[4])#sys.argv[1]#sys.argv[1],sys.argv[3],sys.argv[4]
    

    
     
         
    
    
    



