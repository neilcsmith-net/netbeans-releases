<?php

namespace Test {
class Test {
    private $var = 'abc';
    public function testFunc() {
        echo $this->testFunc();
        return null;
    }
}
}
?>