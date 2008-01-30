;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

XPStyle on

Name "Baralga"
VIProductVersion "${execVersion}"

OutFile "${setup}"

; The default installation directory
InstallDir $PROGRAMFILES\Baralga

; The text to prompt the user to enter a directory
DirText "This will install Baralga on your computer. Choose a directory."

;--------------------------------

; The Installer
Section "" ;No components page, name is not important
  
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; Delete previous contents of install directory
  RMDir /r $INSTDIR
    
  ; Create start menu shortcuts
  CreateDirectory "$SMPROGRAMS\Baralga"
  CreateShortCut "$SMPROGRAMS\Baralga\Baralga.lnk" "$INSTDIR\${execName}"
  
  ; Put file there
  File "${exec}"
  
  ; Copy JRE
  SetOutPath "$INSTDIR\${jreName}"
  File /r "${jre}"
      
  ; Copy Libs
  SetOutPath "$INSTDIR\lib"
  File /r "${libs}"
  
  # define uninstaller name
  writeUninstaller $INSTDIR\uninstaller.exe

  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Baralga" \
                 "DisplayName" "Baralga"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Baralga" \
                 "UninstallString" "$INSTDIR\uninstaller.exe"
SectionEnd ; end the section

# The Uninstaller
Section "Uninstall"

  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Baralga"

  ; Delete start menu shortcuts
  RMDir /r "$SMPROGRAMS\Baralga"
 
  ; Delete previous contents of install directory
  RMDir /r $INSTDIR
 
sectionEnd
