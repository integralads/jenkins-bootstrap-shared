function canonicalize() (
  cd "${1%/*}"
  echo "${PWD}/${1##*/}"
)

if [ -f build.gradle ]; then
  if [ ! x"${JENKINS_WEB:-}" = x ]; then
    export JENKINS_WEB="${JENKINS_WEB%/}"
  fi
  if [ -z "${FORCE_UPGRADE:-}" ]; then
    export JENKINS_USER="admin"
  else
    export JENKINS_USER="${JENKINS_USER:-admin}"
  fi
  export JENKINS_PASSWORD="${JENKINS_PASSWORD:-$(<"${JENKINS_HOME}"/secrets/initialAdminPassword)}"
  unset JENKINS_CALL_ARGS
  "${SCRIPT_LIBRARY_PATH}"/jenkins_call.sh -a -v -v "${JENKINS_WEB}"/api/json -o /dev/null
  export JENKINS_CALL_ARGS="-m POST ${JENKINS_WEB}/scriptText --data-string script= -d"
else
  echo "Not in repository root." 1>&2
fi
