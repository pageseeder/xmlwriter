[ ![Download](https://api.bintray.com/packages/pageseeder/maven/xmlwriter/images/download.svg) ](https://bintray.com/pageseeder/maven/xmlwriter/_latestVersion)

# XML Writer

The XML Writer defines an API and implementations to write XML out to a reading, DOM or SAX.

## Example

```
  XMLWriter xml = new XMLWriterImpl(Reader r);
  xml.openElement("greetings");
  xml.attribute("lang", "en");
  xml.writeText("Hello World");
  xml.closeElement();
```

## Background

We've used this API for over 15 years and it used to be included in Diff-X.
We decided to split it off so that we would not have to include Diff-X in all our projects.
