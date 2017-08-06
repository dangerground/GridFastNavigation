package org.vaadin.patrik.client;

import org.vaadin.patrik.shared.DeleteButtonRendererServerRpc;
import org.vaadin.patrik.shared.DeleteButtonRendererState;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.connectors.ClickableRendererConnector;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.client.renderers.ClickableRenderer.RendererClickHandler;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

@Connect(org.vaadin.patrik.DeleteButtonRenderer.class)
public class DeleteButtonRendererConnector extends ClickableRendererConnector<String> {
	DeleteButtonRendererServerRpc rpc = RpcProxy.create(DeleteButtonRendererServerRpc.class, this);
			
    public class DeleteButtonClientRenderer extends ClickableRenderer<String, Button> {

        private boolean htmlContentAllowed = false;
        private String backgroundColor = null;
        
        @Override
        public Button createWidget() {
            Button b = GWT.create(Button.class);
			backgroundColor = b.getElement().getStyle().getBackgroundColor();
            
            b.addClickHandler(new ClickHandler() {
            	@Override
            	public void onClick(ClickEvent event) {
                    Timer t = null;
            		String style = b.getStyleName();
            		if (style != null && style.contains("delete-confirm")) {
            			if (t != null) t.cancel();
            			b.removeStyleName("delete-confirm");
            			b.setText(getState().delete);
            			MouseEventDetails mouseEventDetails = MouseEventDetailsBuilder
            	                .buildMouseEventDetails(event.getNativeEvent(),
            	                        b.getElement());
            			Element e = b.getElement();
            			e.getStyle().setBackgroundColor(backgroundColor);
            			rpc.onClick(e.getPropertyString("rowKey"),mouseEventDetails);
            		} else {
            			b.setText(getState().confirm);
            			b.setStyleName("delete-confirm");
            			b.getElement().getStyle().setBackgroundColor("red");
            			t = new Timer() {
            				@Override
            				public void run() {
            					b.removeStyleName("delete-confirm");
            					b.setText(getState().delete);
            					Element e = b.getElement();
            				}
            			};
            			t.schedule(10000);
            		}
            		event.stopPropagation();
            	}
            });
            b.setStylePrimaryName("v-nativebutton");
            return b;
        }

        public void setHtmlContentAllowed(boolean htmlContentAllowed) {
            this.htmlContentAllowed = htmlContentAllowed;
        }

        public boolean isHtmlContentAllowed() {
            return htmlContentAllowed;
        }

        @Override
        public void render(RendererCellReference cell, String text, Button button) {

			Element e = button.getElement();

            if(e.getPropertyString("rowKey") != getRowKey((JsonObject) cell.getRow())) {
                e.setPropertyString("rowKey",
                        getRowKey((JsonObject) cell.getRow()));
            }

    		String style = button.getStyleName();
    		if (style != null && style.contains("delete-confirm")) {
    			button.removeStyleName("delete-confirm");
    			button.getElement().getStyle().setBackgroundColor(backgroundColor);
    		}
			button.setText(getState().delete);
    		
        	if (htmlContentAllowed) {
                button.setHTML(text);
            } else {
                button.setText(text);
            }
        }
    }
    	    
@Override
public DeleteButtonClientRenderer getRenderer() {
    return (DeleteButtonClientRenderer) super.getRenderer();
}

@Override
protected Renderer<String> createRenderer() {
	return new DeleteButtonClientRenderer();
}

@Override
protected HandlerRegistration addClickHandler(
        RendererClickHandler<JsonObject> handler) {
    return getRenderer().addClickHandler(handler);
}

@Override
public DeleteButtonRendererState getState() {
    return (DeleteButtonRendererState) super.getState();
}

@Override
public void onStateChanged(StateChangeEvent stateChangeEvent) {
    super.onStateChanged(stateChangeEvent);
    getRenderer().setHtmlContentAllowed(getState().htmlContentAllowed);
}

}
