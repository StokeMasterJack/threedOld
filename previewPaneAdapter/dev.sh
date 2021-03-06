repos=/repos
repo=$repos/threed
lib=/svnCheckouts/lib

src1=$repo/previewPanel/src
src2=$repo/threedFramework/src
src3=$repo/util/src
src4=$repo/previewPane/src
src5=$repo/testHarness/src
src6=$repo/previewPaneAdapter/src

gwtVersion=2.1.0.M3
gwtHome=$lib/gwt/$gwtVersion

lib1=$gwtHome/gwt-dev.jar
lib2=$gwtHome/gwt-user.jar
lib3=$gwtHome/gwt-servlet.jar
lib4=$lib/jsr305/1.0/jsr305.jar
lib5=$lib/gwt-log/3.0.1/gwt-log-3.0.1.jar

warClasses=$war/WEB-INF/classes

modName=com.tms.threed.previewPaneAdapter.PreviewPaneAdapter
startupUrl="TestHarness.html"

#bindAddress=localhost
war=/temp/gwt-jetty/exploded-wars/ppa
war=/Users/dford/Oracle/Middleware/user_projects/domains/base_domain/autodeploy/webdocs/configurator
gen=/temp/gwt-jetty/gen

#g0="-bindAddress $bindAddress "
g1="-war $war "
g2="-gen $gen "
g3="-startupUrl $startupUrl "

j0="-Xmx1028m "
j1="-DconfigDir=/temp/tmsConfig "
j2="-Dlog4j.configuration=file:///temp/tmsConfig/log4j/threed_framework_log4j.xml "

src="$src1:$src2:$src3:$src4:$src5:$src6"
libs=$lib1:$lib2:$lib3:$lib4:$lib5
cp="$src:$libs"

gwtDevModeParams="$g1 $g2 $g3 $modName"
gwtCompileParams="$g1 $g2 -style OBF -XdisableCastChecking $modName"

#-XdisableCastChecking

gwtParams=$gwtDevModeParams
mainClass=com.google.gwt.dev.DevMode
gwtParams=$gwtCompileParams
mainClass=com.google.gwt.dev.Compiler

jvmParams="$j0 $j1 $j2"

echo $jvmParams

#Java vars
jHome=/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home
jExec=$jHome/bin/java

finalCommand="$jExec $jvmParams -classpath $cp $mainClass $gwtParams"

echo $finalCommand

$finalCommand