package net.nutch.util;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;

import net.nutch.fetcher.Outlink;

import org.w3c.dom.*;

/**
 * A collection of methods for extracting content from DOM trees.
 * 
 * This class holds a few utility methods for pulling content out of 
 * DOM nodes, such as getOutlinks, getText, etc.
 *
 */
public class DOMContentUtils {

  /**
   * This method takes a {@link StringBuffer} and a DOM {@link Node},
   * and will append all the content text found beneath the DOM node to 
   * the <code>StringBuffer</code>.
   *
   * <p>
   *
   * If <code>abortOnNestedAnchors</code> is true, DOM traversal will
   * be aborted and the <code>StringBuffer</code> will not contain
   * any text encountered after a nested anchor is found.
   * 
   * <p>
   *
   * Currently, only SCRIPT, STYLE and comment text are ignored.
   *
   * @return true if nested anchors were found
   */
  public static final boolean getText(StringBuffer sb, Node node, 
                                      boolean abortOnNestedAnchors) {
    if (getTextHelper(sb, node, abortOnNestedAnchors, 0)) {
      return true;
    } 
    return false;
  }


  /**
   * This is a convinience method, equivalent to {@link
   * #getText(StringBuffer,Node,boolean) getText(sb, node, false)}.
   * 
   */
  public static final void getText(StringBuffer sb, Node node) {
    getText(sb, node, false);
  }

  // returns true if abortOnNestedAnchors is true and we find nested 
  // anchors
  private static final boolean getTextHelper(StringBuffer sb, Node node, 
                                             boolean abortOnNestedAnchors,
                                             int anchorDepth) {
    if ("script".equalsIgnoreCase(node.getNodeName())) {
      return false;
    }
    if ("style".equalsIgnoreCase(node.getNodeName())) {
      return false;
    }
    if (abortOnNestedAnchors && "a".equalsIgnoreCase(node.getNodeName())) {
      anchorDepth++;
      if (anchorDepth > 1)
        return true;
    }
    if (node.getNodeType() == Node.COMMENT_NODE) {
      return false;
    }
    if (node.getNodeType() == Node.TEXT_NODE) {
      sb.append(node.getNodeValue());
    }
    boolean abort= false;
    NodeList children = node.getChildNodes();
    if ( children != null ) {
      int len = children.getLength();
      for ( int i = 0; i < len; i++ ) {
        if (getTextHelper(sb, children.item(i), 
                          abortOnNestedAnchors, anchorDepth)) {
          abort= true;
          break;
        }
      }
    }
    return abort;
  }

  /**
   * This method takes a {@link StringBuffer} and a DOM {@link Node},
   * and will append the content text found beneath the first
   * <code>title</code> node to the <code>StringBuffer</code>.
   *
   * @return true if a title node was found, false otherwise
   */
  public static final boolean getTitle(StringBuffer sb, Node node) {
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      if ("title".equalsIgnoreCase(node.getNodeName())) {
        getText(sb, node);
        return true;
      }
    }
    NodeList children = node.getChildNodes();
    if (children != null) {
      int len = children.getLength();
      for (int i = 0; i < len; i++) {
        if (getTitle(sb, children.item(i))) {
          return true;
        }
      }
    }
    return false;
  }


  private static boolean hasOnlyWhiteSpace(Node node) {
    String val= node.getNodeValue();
    for (int i= 0; i < val.length(); i++) {
      if (!Character.isWhitespace(val.charAt(i)))
        return false;
    }
    return true;
  }

  // this only covers a few cases of empty links that are symptomatic
  // of nekohtml's DOM-fixup process...
  private static boolean shouldThrowAwayLink(Node node, NodeList children, 
                                              int childLen) {
    if (node.getNodeName().equalsIgnoreCase("area")) {
      return false;
    }
    if (childLen == 0) {
      // this has no inner structure 
      return true;
    } else if ((childLen == 1) 
               && (children.item(0).getNodeType() == Node.ELEMENT_NODE)
               && ("a".equalsIgnoreCase(children.item(0).getNodeName()))) { 
      // single nested link
      return true;

    } else if (childLen == 2) {

      Node c0= children.item(0);
      Node c1= children.item(1);

      if ((c0.getNodeType() == Node.ELEMENT_NODE)
          && ("a".equalsIgnoreCase(c0.getNodeName()))
          && (c1.getNodeType() == Node.TEXT_NODE) 
          && hasOnlyWhiteSpace(c1) ) {
        // single link followed by whitespace node
        return true;
      }

      if ((c1.getNodeType() == Node.ELEMENT_NODE)
          && ("a".equalsIgnoreCase(c1.getNodeName()))
          && (c0.getNodeType() == Node.TEXT_NODE) 
          && hasOnlyWhiteSpace(c0) ) {
        // whitespace node followed by single link
        return true;
      }

    } else if (childLen == 3) {
      Node c0= children.item(0);
      Node c1= children.item(1);
      Node c2= children.item(2);
      
      if ((c1.getNodeType() == Node.ELEMENT_NODE)
          && ("a".equalsIgnoreCase(c1.getNodeName()))
          && (c0.getNodeType() == Node.TEXT_NODE) 
          && (c2.getNodeType() == Node.TEXT_NODE) 
          && hasOnlyWhiteSpace(c0)
          && hasOnlyWhiteSpace(c2) ) {
        // single link surrounded by whitespace nodes
        return true;
      }
    }

    return false;
  }

  /**
   * This method finds all anchors below the supplied DOM
   * <code>node</code>, and creates appropriate {@link Outlink}
   * records for each (relative to the supplied <code>base</code>
   * URL), and adds them to the <code>outlinks</code> {@link
   * ArrayList}.
   *
   * <p>
   *
   * Links without inner structure (tags, text, etc) are discarded, as
   * are links which contain only single nested links and empty text
   * nodes (this is a common DOM-fixup artifact, at least with
   * nekohtml).
   */
  public static final void getOutlinks(URL base, ArrayList outlinks, 
                                       Node node) {

    NodeList children = node.getChildNodes();
    int childLen= 0;
    if (children != null)
      childLen= children.getLength();
  
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      if ("a".equalsIgnoreCase(node.getNodeName()) ||
          "area".equalsIgnoreCase(node.getNodeName())) {

        if (shouldThrowAwayLink(node, children, childLen)) {
          // this has no inner structure or just a single nested
          // anchor-- toss it!
        } else {

          StringBuffer linkText = new StringBuffer();
          getText(linkText, node, true);

          NamedNodeMap attrs = node.getAttributes();
          String target= null;
          for (int i= 0; i < attrs.getLength(); i++ ) {
            if ("href".equalsIgnoreCase(attrs.item(i).getNodeName())) {
              target= attrs.item(i).getNodeValue();
              break;
            }
          }
          if (target != null)
            try {
              URL url = new URL(base, target);
              outlinks.add(new Outlink(url.toString(),
                                       linkText.toString().trim()));
            } catch (MalformedURLException e) {
              // don't care
            }
        }
      }
    }
    for ( int i = 0; i < childLen; i++ ) {
      getOutlinks(base, outlinks, children.item(i));
    }
  }

}

