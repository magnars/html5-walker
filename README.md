# html5-walker

A thin Clojure wrapper around
[jfiveparse](https://github.com/digitalfondue/jfiveparse), this lets you find
and replace in HTML5 strings.

## Install

- add `[html5-walker "2023.10.22"]` to `:dependencies` in your project.clj

or

- add  `html5-walker {:mvn/version "2023.10.22"}` to `:deps` in your deps.edn

## Usage

html5-walker exposes these functions:

### html5-walker.walker/find-nodes

Signature: `(find-nodes html-string path)`

It returns a sequence of
[Nodes](https://static.javadoc.io/ch.digitalfondue.jfiveparse/jfiveparse/0.6.0/ch/digitalfondue/jfiveparse/Node.html)
matching the path.

A path is a vector of symbols (or strings) of CSS selectors. Like this:

- `'[a]` matches all anchor tags.
- `'[form input]` matches all input tags nested inside a form.
- `'[form > input]` matches all input tags that are direct children of a form.
- `'[div.foo]` matches all div tags with "foo" in its class name.
- `'[.button]` matches all elements with the "button" class.
- `'[div#content]` matches the div with "content" as its id.
- `'[:first-child]` matches any element that is the first child.
- `'[:last-child]` matches any element that is the last child.
- `'["meta[property]"]` matches all meta tags with the `property` attribute.
- `'["meta[property=og:title]"]` matches all meta tags with the `property`
  attribute set to "og:title".

The following additional attribute selectors are also supported, and work like
they do in CSS: `*=`, `$=`, `~=` and `^=`.

So running:

```clj
(require '[html5-walker.walker :as walker])

(walker/find-nodes "<ul><li>1</li><li>2</li></ul>" '[ul li])
```

would return a sequence with two `li` nodes in it. [See the javadoc for more
information about these
nodes.](https://static.javadoc.io/ch.digitalfondue.jfiveparse/jfiveparse/0.6.0/ch/digitalfondue/jfiveparse/Node.html)

### html5-walker.walker/replace-in-fragment

Signature: `(replace-in-fragment html-string path->fn)`

This returns a new html-string with any changes performed by the functions in
the `path->fn` map applied.

So running:

```clj
(require '[html5-walker.walker :as walker])

(walker/replace-in-fragment
  "<ul><li>1</li><li>2</li></ul>"
  {'[ul li] (fn [node] (.setInnerHTML node (str (.getInnerHTML node) "!!!")))})
```

would return:

```
"<ul><li>1!!!</li><li>2!!!</li></ul>"
```

### html5-walker.walker/replace-in-document

Just like `replace-in-fragment`, except it works on an entire html document.
This means that `html`, `head` and `body` tags are expected to be there. They
will be added if missing.

Note that `replace-in-fragment` will actually remove these tags when found.

## More usage

Take a look at the tests if you'd like more examples.

## About `html5-walker.core`

The `html5-walker.core` namespace contains the same three functions as above.
These functions work exactly the same, with one important difference: `'[div a]`
is treated as `'[div > a]`, e.g. a direct child selector. This was the library's
original behavior, and the core namespace is kept around for backwards
compatibility.

## Changes

#### 2023.10.22

- Rename namespace to `html5-walker.walker` to preserve backwards compatibility
  for `html5-walker.core`
- Support lots more CSS selector semantics [(cjohansen)](https://github.com/cjohansen)

#### 2022.03.07

- Upgrade jfiveparse to version 0.9.0
- Switch versioning separator to dot, for more Maven friendly version numbers

#### 2020-01-08

- Support selecting only by class name, like so: `:.myclass`

## License

Copyright © Magnar Sveen, since 2019

Distributed under the Eclipse Public License, the same as Clojure.
