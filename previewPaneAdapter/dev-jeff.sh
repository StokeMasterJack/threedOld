r=/home/jshelley/dev/workspaces/toyota
lib=$r/lib
nfc=$r/NonFlashConfig
threed=$r/Threed_Framework

src=src:$nfc/src/java:$threed/src/java

#gwtVersion=2.0.3
gwtVersion=2.0
gwtBase=$lib/gwt/$gwtVersion
gwtCp=$gwtBase/gwt-dev.jar:$gwtBase/gwt-user.jar:$gwtBase/gwt-servlet.jar

cp=$gwtCp:$lib/jsr305/1.0/jsr305.jar:$lib/jackson/1.5/jackson-core-asl-1.5.0.jar:$lib/jackson/1.5/jackson-mapper-asl-1.5.0.jar:$lib/gwt-log/gwt-log-3.0.1.jar:/temp/gwt-jetty/exploded-wars/pp/WEB-INF/classes:$src

#style=PRETTY
style=OBF
war=/temp/gwt-jetty/exploded-wars/pp
gen=/temp/gwt-jetty/gen

#startupUrl=PreviewPanelTest.html
#modName=com.tms.nonFlashConfig.previewPanel.PreviewPanelTest

#startupUrl=PreviewPaneTest.jsp?seriesCategory=28
startupUrl=PreviewPanelAdaptor.html
modName=com.tms.configurator.PreviewPanelAdaptor

#mainClass=com.google.gwt.dev.DevMode
gwtParams="-war $war -gen $gen -startupUrl $startupUrl $modName"

mainClass=com.google.gwt.dev.Compiler
gwtParams="-war $war -style $style -gen $gen $modName"


#Java vars
jHome=/usr/lib/jvm/java-6-sun
jExec=$jHome/bin/java
jvmParams="-Xmx512m -Dfile.encoding=MacRoman -Dlog4j.configuration=file:///temp/tmsConfig/log4j/threed_framework_log4j.xml"


allJavaParams="$jvmParams -classpath $cp $mainClass $gwtParams"

finalCommand="$jExec $allJavaParams"

echo $finalCommand
#echo $src

$finalCommand
