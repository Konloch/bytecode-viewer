#!/bin/sh

# the script will install the dependencies from ./libs/ diretory into the local Maven repository

install() {
  if [ ! -z "$5" ]; then
    classifier=-Dclassifier=$5
  else
    unset classifier
  fi
  mvn install:install-file -Dfile=libs/$1 -DgroupId=$2 -DartifactId=$3 -Dversion=$4 ${classifier} -Dpackaging=jar 
}

install procyon-decompiler-0.5.30.jar com.strobel procyon 0.5.30
install cfr_0_115.jar org.benf cfr 0.115
install jd-gui-1.0.0-RC4.jar ca.benow jd 1.4.0
install byteanalysis-1.0.jar eu.bibl byteanalysis 1.0
install apktool_2.0.1_obf-2.jar brut apktool 2.0.1
install fernflower-2016.jar org.jetbrains.java decompiler 2015-1
install dex_obf.jar dex2jar dex2jar 0.0.1
install smali-2.0.3-obf-patched.jar org.jf smali 2.0.3 patched
install baksmali-2.0.3_obf.jar org.jf baksmali 2.0.3
