cd storage/downloads
rm -rf <folder_name>
git clone <repo_url> <folder_name>
cd <folder_name>
ls
echo <output_separator>
echo $(grep -m 1 '^#' README.md)
