repo=/Users/dford/smart-soft/clients/tms/svnFun/tmsThreed/trunk
lib=/Users/dford/smart-soft/clients/tms/svnFun/tmsTools/lib

src1=$repo/previewPanel/src
src2=$repo/threedFramework/src
src3=$repo/imageModel/src
src4=$repo/featureModel/src
src5=$repo/featurePicker/src
src6=$repo/threedCore/src
src7=$repo/util/src
src8=$repo/previewPane/src
src9=$repo/testHarness/src
src10=$repo/gwtCollections/src
src11=$repo/gwtCollections/super

gwtVersion=2.0.3
gwtHome=$lib/gwt/$gwtVersion

lib1=$gwtHome/gwt-dev.jar
lib2=$gwtHome/gwt-user.jar
lib3=$gwtHome/gwt-servlet.jar
lib4=$lib/jsr305/1.0/jsr305.jar
lib5=$lib/gwt-log/3.0.1/gwt-log-3.0.1.jar

warClasses=$war/WEB-INF/classes

modName=com.tms.threed.previewPane.PreviewPaneTest
startupUrl="previewPaneTest.html"

#bindAddress=localhost
war=/temp/gwt-jetty/exploded-wars/previewPaneTest
gen=/temp/gwt-jetty/gen

#g0="-bindAddress $bindAddress "
g1="-war $war "
g2="-gen $gen "
g3="-startupUrl $startupUrl "

j0="-Xmx1028m "
j1="-DconfigDir=/temp/tmsConfig "
j2="-Dlog4j.configuration=file:///temp/tmsConfig/log4j/threed_framework_log4j.xml "

src="$src1:$src2:$src3:$src4:$src5:$src6:$src7:$src8:$src9"
libs=$lib1:$lib2:$lib3:$lib4:$lib5
cp="$src:$libs"

gwtDevModeParams="$g1 $g2 $g3 $modName"
gwtCompileParams="$g1 $g2 -style PRETTY -XdisableCastChecking $modName"

gwtParams=$gwtDevModeParams
#gwtParams=$gwtCompileParams

mainClass=com.google.gwt.dev.DevMode
#mainClass=com.google.gwt.dev.Compiler

jvmParams="$j3 $j0 $j1 $j2"

echo $jvmParams

#Java vars
jHome=/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home
jExec=$jHome/bin/java

finalCommand="$jExec $jvmParams -classpath $cp $mainClass $gwtParams"

echo $finalCommand

$finalCommand
