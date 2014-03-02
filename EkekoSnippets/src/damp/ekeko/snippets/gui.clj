(ns 
  ^{:doc "GUI-related functionality of snippet-driven querying."
    :author "Coen De Roover, Siltvani"}
  damp.ekeko.snippets.gui
  (:import 
    [org.eclipse.jdt.core.dom ASTNode CompilationUnit]
    [org.eclipse.jface.viewers TreeViewerColumn]
    [org.eclipse.swt SWT]
    [org.eclipse.ui IWorkbench PlatformUI IWorkbenchPage IWorkingSet IWorkingSetManager]
    [org.eclipse.swt.widgets Display])
  (:require [damp.ekeko.snippets 
             [util :as util]
             [snippet :as snippet]
             [snippetgroup :as snippetgroup]
             [operatorsrep :as operatorsrep]
             ])
  (:require [damp.ekeko.jdt [astnode :as astnode]])
  (:require [damp.ekeko.gui]))

; Callbacks for TemplateTreeContentProvider
; -----------------------------------------

;;the following explicitly avoid creating new values that are not yet in the snippet datastructure


;(remove (fn [x] (astnode/primitivevalue? x))
(defn
  templatetreecontentprovider-children
  [snippetgroup val]
  ;treeview children of given treeview parent
  (to-array 
    (mapcat 
      (fn [snippet] 
        (snippet/snippet-node-children snippet val))
      (snippetgroup/snippetgroup-snippetlist snippetgroup))))
    
 
(defn
  templatetreecontentprovider-parent
  [snippetgroup c]
  ;treeview parent of given treeview child
  (some 
    (fn [snippet] 
      (snippet/snippet-node-owner snippet c))
    (snippetgroup/snippetgroup-snippetlist snippetgroup)))
    
(defn
  templatetreecontentprovider-elements
  [snippetgroup input]
  ;roots of treeview
  (when 
    (= input snippetgroup)
    (to-array (map snippet/snippet-root (snippetgroup/snippetgroup-snippetlist snippetgroup)))))
  

;; Callbacks for TemplateTreeLabelProvider
;; ---------------------------------------

(defn
  templatetreelabelprovider-node
  [snippet element]
  (cond 
    (astnode/nilvalue? element)
    "null"
    (astnode/value? element)
    (str (:value element))
    :else 
    (str element)))

(defn
  templatetreelabelprovider-kind
  [snippet element]
  (if
    (astnode/ast? element)
    (.getSimpleName (class element))
    ""))

(defn
  templatetreelabelprovider-property
  [snippet element]
  (let [owner (astnode/owner element)
        property (astnode/owner-property element)
        id (astnode/property-descriptor-id property)
        idwithowner (str id " of " (.getSimpleName (class owner)))]
    (if 
      (astnode/property-descriptor-list? property)
      (if 
        (astnode/lstvalue? element)
        (str "list " idwithowner)
        (str "element of " idwithowner))
      idwithowner)))


; Callbacks for OperatorTreeContentProvider
; -----------------------------------------

(defn- 
  operator-category?
  [operator-or-category]
  (keyword?  operator-or-category))

(defn
  operatortreecontentprovider-children
  [snippetnode operator-or-category]
  ;treeview children of given treeview parent
  (to-array 
    (if
      (operator-category? operator-or-category)
      (operatorsrep/applicable-operators-in-category snippetnode operator-or-category)
      [])))
      
 
(defn
  operatortreecontentprovider-parent
  [snippetnode operator-or-category]
  (when-not
    (operator-category? operator-or-category)
    (operatorsrep/operator-category operator-or-category)))
  
    
(defn
  operatortreecontentprovider-elements
  [snippetnode input]
  ;roots of treeview
  (when 
    (= snippetnode input)
    (to-array (operatorsrep/registered-categories))))
  

;; OperatorTreeLabelProvider
;; -----------------------------

(defn
  operatortreelabelprovider-operator
  [element]
  (if
    (operator-category? element)
    (operatorsrep/category-description element)
    (operatorsrep/operator-name element)))
  
  

;; Opening  TemplateView programmatically
;; --------------------------------------

(def templateview-cnt (atom 0))

(defn 
  open-templateview
  [snippetgroup]
  (let [page (-> (PlatformUI/getWorkbench)
               .getActiveWorkbenchWindow ;nil if called from non-ui thread 
               .getActivePage)
        qvid (damp.ekeko.snippets.gui.TemplateView/ID)
        uniqueid (str @templateview-cnt)
        viewpart (.showView page qvid uniqueid (IWorkbenchPage/VIEW_ACTIVATE))]
    (swap! templateview-cnt inc)
    (.setViewID viewpart uniqueid)
    (.setInput (.getViewer viewpart) snippetgroup)
    viewpart))


(defn
  view-template
  [snippetgroup]
  (damp.ekeko.gui/eclipse-uithread-return (fn [] (open-templateview snippetgroup))))




;; Callbacks for operand binding cell editor
;; -----------------------------------------


(defn
  make-groupnode-selectiondialog
  [shell group template node]
  (damp.ekeko.snippets.gui.TemplateGroupNodeSelectionDialog. shell group template node))


(defmulti
  operandbinding-celleditor
  (fn [table operandbinding]
    (operatorsrep/operand-scope (operatorsrep/binding-operand operandbinding))))

(defmethod 
  operandbinding-celleditor
  operatorsrep/opscope-subject
  [table operandbinding]
  (let [editor 
        (proxy [org.eclipse.jface.viewers.DialogCellEditor] [table]
          (openDialogBox [window] 
            (let [shell (.getShell window)
                  dialog (make-groupnode-selectiondialog 
                           shell 
                           (operatorsrep/binding-group operandbinding)
                           (operatorsrep/binding-template operandbinding)
                           (operatorsrep/binding-value operandbinding))]
              (.getValue (.open dialog)))))]
        editor))

(defmethod 
  operandbinding-celleditor
  operatorsrep/opscope-variable 
  [table operandbinding]
  (let [editor (org.eclipse.jface.viewers.TextCellEditor. table)]
    editor))
    
  
(defn
  operandbinding-labelprovider-descriptiontext
  [operandbinding]
  (operatorsrep/operand-description (operatorsrep/binding-operand operandbinding)))

(defmulti
  operandbinding-labelprovider-valuetext
  (fn [operandbinding]
    (operatorsrep/operand-scope (operatorsrep/binding-operand operandbinding))))

(defmethod
  operandbinding-labelprovider-valuetext
  operatorsrep/opscope-subject
  [operandbinding]
  ;;todo: invoke prettyprinter on node in template (also accessible from operatorbinding)
  (str (operatorsrep/binding-value operandbinding)))

(defmethod
  operandbinding-labelprovider-valuetext
  :default
  [operandbinding]
  ;;todo: invoke prettyprinter on node in template (also accessible from operatorbinding)
  (str (operatorsrep/binding-value operandbinding)))



(defn
  configure-callbacks
  []
  (set! (damp.ekeko.snippets.gui.TemplateTreeContentProvider/FN_ELEMENTS) templatetreecontentprovider-elements)
  (set! (damp.ekeko.snippets.gui.TemplateTreeContentProvider/FN_CHILDREN) templatetreecontentprovider-children)
  (set! (damp.ekeko.snippets.gui.TemplateTreeContentProvider/FN_PARENT) templatetreecontentprovider-parent)
  (set! (damp.ekeko.snippets.gui.TemplateTreeLabelProviders/FN_LABELPROVIDER_NODE) templatetreelabelprovider-node)
  (set! (damp.ekeko.snippets.gui.TemplateTreeLabelProviders/FN_LABELPROVIDER_KIND) templatetreelabelprovider-kind)
  (set! (damp.ekeko.snippets.gui.TemplateTreeLabelProviders/FN_LABELPROVIDER_PROPERTY) templatetreelabelprovider-property)
  
  (set! (damp.ekeko.snippets.gui.OperatorTreeContentProvider/FN_ELEMENTS) operatortreecontentprovider-elements) 
  (set! (damp.ekeko.snippets.gui.OperatorTreeContentProvider/FN_CHILDREN) operatortreecontentprovider-children)
  (set! (damp.ekeko.snippets.gui.OperatorTreeContentProvider/FN_PARENT) operatortreecontentprovider-parent) 
  (set! (damp.ekeko.snippets.gui.OperatorTreeLabelProvider/FN_LABELPROVIDER_OPERATOR)  operatortreelabelprovider-operator)
      
  (set! (damp.ekeko.snippets.gui.OperandBindingEditingSupport/FN_OPERANDBINDING_EDITOR) operandbinding-celleditor)
  (set! (damp.ekeko.snippets.gui.OperandBindingLabelProviderDescription/FN_LABELPROVIDER_DESCRIPTION_TEXT) operandbinding-labelprovider-descriptiontext)
  (set! (damp.ekeko.snippets.gui.OperandBindingLabelProviderValue/FN_LABELPROVIDER_DESCRIPTION_VALUE) operandbinding-labelprovider-valuetext)

  
  )


(configure-callbacks)


  
  
  
