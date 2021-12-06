#if( $containerObject )
#set( $repoName = "${aib.getApplicationNameFormatted()}.${containerObject.getName()}" )
#else
#set( $repoName = ${aib.getParam("git.repository")} )
#end
#set( $gitTag = ${aib.getParam("git.tag")} )
#!/bin/bash

cd $1

git config --global user.email "${user.getEmail()}"
git config --global user.name "${user.getFullName()}"
 
echo init the repository
git init .

echo add all files from root dir below, with ignore dirs and files in the .gitignore
git add .

echo 'commit all the files'
git commit -m "initial commit"

echo 'add a remote pseudo for the ${repoName} repository'

#set( $credential = ${user.getGitToken()} )
#if ( $credential == $null || $credential == "" ) 
#set( $credential = ${aib.getParam("git.password")} )
#end##if ( $credential == $null || $credential == "" )
git remote add ${repoName} https://${aib.getParam("git.username")}:${credential}@${aib.getParam("git.host")}/${aib.getParam("git.username")}/${aib.getParam("git.repository")}

echo 'push the commits to the repository master'
git push ${repoName} master

#if ( $gitTag == $null || $gitTab == "" ) 
#else
echo 'delete tag ${gitTag}'
git tag -d ${gitTag}
git push --delete ${repoName} ${gitTag}

echo 'add tag ${gitTag}'
git tag ${gitTag}

echo 'push tag ${gitTag}'
git push ${repoName} ${gitTag}
#end

