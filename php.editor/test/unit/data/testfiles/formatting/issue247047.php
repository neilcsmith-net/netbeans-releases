<?php

$foo = array(
    "foo" => function ($args) {
        print_r(array(1, 2, 3));
    },
        "foo" => function ($args) {
        echo "";
    },
    );

class A {

    public function func() {
        [
            'func' => function() {
                $array = [];
            }
            ];
        }

    }
