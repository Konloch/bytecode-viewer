.version 53 0
.class module module-info
.super [0]
.module 'bar.foo' version [0]
    .requires 'java.base' transitive version '9'
    .requires 'base.java' static_phase version [0]
    .exports bar/foo to test test2 'bar.foo'
    .opens bar/foo
.end module
.end class
