# html5-walker

A thin Clojure wrapper around
[jfiveparse](https://github.com/digitalfondue/jfiveparse), this lets you find
and replace in HTML5 strings.

## Install

- add `[html5-walker "2022.03.07"]` to `:dependencies` in your project.clj

or

- add  `html5-walker {:mvn/version "2022.03.07"}` to `:deps` in your deps.edn

## Usage

html5-walker exposes these functions:

### find-nodes

Signature: `(find-nodes html-string path)`

It returns a sequence of
[Nodes](https://static.javadoc.io/ch.digitalfondue.jfiveparse/jfiveparse/0.6.0/ch/digitalfondue/jfiveparse/Node.html)
matching the path.

A path is a vector of keywords of element names, with optional hiccup-esque class names. Like this:

- `[:a]` matches all anchor tags.
- `[:form :input]` matches all input tags nested inside a form.
- `[:div.foo]` matches all div tags with "foo" in its class name.

So running:

```clj
(find-nodes "<ul><li>1</li><li>2</li></ul>" [:ul :li])
```

would return a sequence with two `li` nodes in it. [See the javadoc for more
information about these
nodes.](https://static.javadoc.io/ch.digitalfondue.jfiveparse/jfiveparse/0.6.0/ch/digitalfondue/jfiveparse/Node.html)

### replace-in-fragment

Signature: `(replace-in-fragment html-string path->fn)`

This returns a new html-string with any changes performed by the functions in the `path->fn` map applied.

So running:

```clj
(replace-in-fragment
  "<ul><li>1</li><li>2</li></ul>"
  {[:ul :li] (fn [node] (.setInnerHTML node (str (.getInnerHTML node) "!!!")))})
```

would return:

```
"<ul><li>1!!!</li><li>2!!!</li></ul>"
```

### replace-in-document

Just like `replace-in-fragment`, except it works on an entire html document.
This means that `html`, `head` and `body` tags are expected to be there. They
will be added if missing.

Note that `replace-in-fragment` will actually remove these tags when found.

## More usage

Take a look at the tests if you'd like more examples.

## Changes

#### 2022.03.07

- Upgrade jfiveparse to version 0.9.0
- Switch versioning separator to dot, for more Maven friendly version numbers

#### 2020-01-08

- Support selecting only by class name, like so: `:.myclass`

## License

Copyright © Magnar Sveen, since 2019

Distributed under the Eclipse Public License, the same as Clojure.
