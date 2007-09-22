
Name "Baralga"

; The default installation directory
InstallDir $PROGRAMFILES\Baralga

; The text to prompt the user to enter a directory
DirText "This will install Baralga on your computer. Choose a directory."

;--------------------------------

; The stuff to install
Section "" ;No components page, name is not important

  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; Put file there
  File "${exec}"
  
  SetOutPath "$INSTDIR\${jreName}"
  
  File /r "${jre}"
  
SectionEnd ; end the section

OutFile "${setup}"
