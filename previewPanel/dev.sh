projects=/Users/dford/smart-soft/clients/tms/_projects
repo=$projects/tmsRepoThreed
lib=$projects/tmsRepoThreedTools/lib

src1=$repo/previewPanel/src
src2=$repo/threedFramework/src
src3=$repo/threedCore/src
src4=$repo/imageModel/src
src5=$repo/util/src

gwtVersion=2.0.3
gwtHome=$lib/gwt/$gwtVersion



lib1=$gwtHome/gwt-dev.jar
lib2=$gwtHome/gwt-user.jar
lib3=$gwtHome/gwt-servlet.jar
lib4=$lib/jsr305/1.0/jsr305.jar
lib5=$lib/gwt-log/3.0.1/gwt-log-3.0.1.jar

bindAddress=localhost
war=/temp/gwt-jetty/exploded-wars/previewPanelTest
gen=/temp/gwt-jetty/gen

warClasses=$war/WEB-INF/classes

modName=com.tms.threed.previewPanel.PreviewPanelTest
startupUrl=com.tms.threed.previewPanel.PreviewPanelTest/PreviewPanelTest.html

mainClass=com.google.gwt.dev.DevMode

g0="style $bindAddress "
g0="-bindAddress $bindAddress "
g1="-war $war "
g2="-gen $gen "
g3="-startupUrl $startupUrl "

j0="-Xmx1028m "
j1="-DconfigDir=/temp/tmsConfig "
j2="-Dlog4j.configuration=file:///temp/tmsConfig/log4j/threed_framework_log4j.xml "

src="$src1:$src2:$src3:$src4:$src5:$src6"
libs=$lib1:$lib2:$lib3:$lib4:$lib5
cp="$src:$warClasses:$libs"
gwtParams="$g0 $g1 $g2 $g3 $modName"
jvmParams="$j0 $j1 $j2"

#Java vars
jHome=/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home
jExec=$jHome/bin/java

jvmParams="-Xmx1028m -DconfigDir=/temp/tmsConfig -Dlog4j.configuration=file:///temp/tmsConfig/log4j/threed_framework_log4j.xml"


allJavaParams="$jvmParams -classpath $cp $mainClass $gwtParams"

finalCommand="$jExec $jvmParams -classpath $cp $mainClass $gwtParams"

echo "$mainClass $gwtParams"

$finalCommand