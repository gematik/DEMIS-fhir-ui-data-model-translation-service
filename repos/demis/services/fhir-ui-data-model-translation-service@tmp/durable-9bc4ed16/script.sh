
            git add -A
            GIT_COMMITTER_NAME="svc_gitlab" GIT_COMMITTER_EMAIL="svc_gitlab@gematik.de" git commit -m "DEMIS-0: updated spring-parent to 2.15.4" --author "svc_gitlab <svc_gitlab@gematik.de>" || echo ">> Nothing new to commit on this branch. Proceed...";
            git tag NotUsed
        