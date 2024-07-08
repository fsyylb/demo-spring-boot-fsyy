```shell script
#!/bin/bash

set -e
usage() { echo -e "Usage: $0 [-p partyID] [-i ipAddress]"; }

PartyID=''
IpAddress=''

while getopts "hp:i:" arg; do
    case $arg in
    p)
      PartyID=${OPTARG}
      ;;
    i)
      IpAddress=${OPTARG}
      ;;
    h)
      usage
      exit 1
      ;;
    *)
      echo "Invalid option: -$arg" >&2
      usage
      exit2
      ;;
    esac
done

```


```shell script
#!/bin/bash
function load_images(){
  for images in `ls ./images`
  do
    docker load -i ./images/$images
  done
}

load_images

```

```shell script

```