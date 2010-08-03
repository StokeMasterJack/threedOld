package com.tms.threed.jpgGen.singleJpgGen;

abstract public class JpgGenerator {

    protected final JpgSpec spec;

    protected JpgGenerator(JpgSpec spec) {
        this.spec = spec;
    }

    public void generate() {
        spec.prePreGen();
        if(spec.jpgAlreadyCreated() && !spec.isOverwriteMode()) return;
        this.doGen();
    }

    abstract protected void doGen();


}
