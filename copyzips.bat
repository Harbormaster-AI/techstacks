 pushd d:\downloads
   for /r %%a in (*.zip) do (
     COPY "%%a" ".\%%~nxa"
   )
   popd