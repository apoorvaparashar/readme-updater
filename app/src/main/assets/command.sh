cd storage/downloads

CLONE_REPO=false

if [ -d "<folder_name>/.git" ]; then
    CURRENT_URL=$(git -C "<folder_name>" config --get remote.origin.url)

    if [ "$CURRENT_URL" = "<repo_url>" ]; then
        git -C "<folder_name>" pull
    else
        CLONE_REPO=true
    fi
else
    CLONE_REPO=true
fi

if [ "$CLONE_REPO" = true ]; then
    rm -rf "<folder_name>"
    git clone "<repo_url>" "<folder_name>"
fi

cd <folder_name>

echo <output_separator>

echo $(grep -m 1 '^#' README.md)
