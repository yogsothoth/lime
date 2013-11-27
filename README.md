# Introduction to lime

lime is a ray tracer written in clojure. It aims to be fun to play with, both for users and for coders.

## Usage
lime expect a scene file, describing the objects, lights, materials, etc. This file must be written in clojure. Other options and parameters specify how the image must be rendered: height, width, sampling algorithm, number of samples per pixel and zoom factor.

The expected parameters and options are as follows:

    Mandatory options
        -f or --file: the path to scene file (see documentation above)
        -o or --output: the path to the output file (picture file)
        -h or --height: the height of the rendered picture
        -w or --width: the width of the rendered picture
    Optional, errr, options:
        None at the moment.

Whenever a mandatory options is not provided on the command line, lime prints an error message and exits.

## Scene files

lime expects the user to provide lime with a scene file, written in clojure, and specified on the command line behind the `-f` or `--file` options. The scene file must be written in clojure, must contain a single function that returns a world structure. This file will be read and evaluated by lime.

# License
Copyright (c) 2013 Nicolas Herry
Distributed under the GNU General Public License, modified to allow linking with Clojure. See the file LICENSE at the root of the distribution.
