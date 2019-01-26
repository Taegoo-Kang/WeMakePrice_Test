package com.price.make.we;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import com.price.make.we.dto.ParameterDto;
import com.price.make.we.dto.ResultDto;
import com.price.make.we.dto.Type;
import com.price.make.we.service.TextService;
import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("valo")
@SpringUI(path="/")
public class AppUI extends UI {
	private static final long serialVersionUID = -6099580473148096393L;
	
	@Autowired
	private TextService textService;
	
	private VerticalLayout vLayout;
	private TextField tfUrl;
	private ComboBox<Type> cboType;
	private TextField tfBind;
	private Button btnPrint;
	private TextArea taQuot;
	private TextArea taRemain;

	@Override
	protected void init(VaadinRequest request) {
		
		vLayout = new VerticalLayout();
		vLayout.setSpacing(true);
		vLayout.setMargin(true);
		vLayout.setHeight("100%");
		vLayout.setWidth("720px");
		setContent(vLayout);
		
		initUrlTextFiled();
		initComboBox();
		initBindTextField();
		initButton();
		initTextAreas();
	}
	
	public void initUrlTextFiled() {
		
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setWidth("100%");
		
		Label label = new Label("URL");
		
		tfUrl = new TextField();
		tfUrl.setWidth("100%");
		tfUrl.setRequiredIndicatorVisible(true);
		tfUrl.setPlaceholder("http://..");
		
		hLayout.addComponent(label);
		hLayout.addComponent(tfUrl);
		hLayout.setExpandRatio(tfUrl, 1.0f);
		hLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		hLayout.setComponentAlignment(tfUrl, Alignment.MIDDLE_LEFT);
		
		vLayout.addComponent(hLayout);
	}
	
	public void initComboBox() {
		
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setWidth("100%");
		
		Label label = new Label("Type");
		
		List<Type> types = new ArrayList<>();
		types.add(Type.HTML);
		types.add(Type.TEXT);
		
		cboType = new ComboBox<>();
		cboType.setTextInputAllowed(false);
		cboType.setEmptySelectionAllowed(false);
		cboType.setItems(types);
		cboType.setValue(Type.HTML);
		
		hLayout.addComponent(label);
		hLayout.addComponent(cboType);
		hLayout.setExpandRatio(cboType, 1.0f);
		hLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		hLayout.setComponentAlignment(cboType, Alignment.MIDDLE_LEFT);
		
		vLayout.addComponent(hLayout);
	}
	
	public void initBindTextField() {
		
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setWidth("100%");
		
		Label label = new Label("출력 묶음 단위 자연수");
		
		tfBind = new TextField();
		tfBind.setRequiredIndicatorVisible(true);
		
		hLayout.addComponent(label);
		hLayout.addComponent(tfBind);
		hLayout.setExpandRatio(tfBind, 1.0f);
		hLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		hLayout.setComponentAlignment(tfBind, Alignment.MIDDLE_LEFT);
		
		vLayout.addComponent(hLayout);
	}
	
	public void initButton() {
		
		final Pattern pattern = Pattern.compile("^[0-9]*$");
		
		btnPrint = new Button("출력", event -> {
			
			String url = tfUrl.getValue();
			String bind = tfBind.getValue();
			
			if(url == null || url.isEmpty()) {
				Notification.show("URL을 입력해주세요.", Notification.Type.WARNING_MESSAGE);
				tfUrl.focus();
				return;
			}
			if(bind == null || bind.isEmpty()) {
				Notification.show("자연수를 입력해주세요.", Notification.Type.WARNING_MESSAGE);
				tfBind.focus();
				return;
			}
			if(!pattern.matcher(bind).matches() || Integer.valueOf(bind) < 1) {
				Notification.show("자연수만 입력 가능합니다.", Notification.Type.WARNING_MESSAGE);
				tfBind.focus();
				return;
			}
			
			ParameterDto param = new ParameterDto();
			param.setUrl(url);
			param.setType(cboType.getValue());
			param.setBind(Integer.valueOf(bind));
			
			try {
				ResultDto result = textService.getTextBind(param);
				List<String> quots = result.getQuotient();
				StringBuilder quotBuilder = new StringBuilder();
				for(String quot : quots) {
					if(quotBuilder.length() != 0) {
						quotBuilder.append(", ");
					}
					quotBuilder.append(quot);
				}
				
				taQuot.setValue(quotBuilder.toString());
				taRemain.setValue(result.getRemainder());
				
			} catch(MalformedURLException e) {
				e.printStackTrace();
				Notification.show("URL을 정확히 입력해주세요.", Notification.Type.WARNING_MESSAGE);
				tfUrl.focus();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		btnPrint.setClickShortcut(KeyCode.ENTER, null);
		
		vLayout.addComponent(btnPrint);
		vLayout.setComponentAlignment(btnPrint, Alignment.MIDDLE_RIGHT);
	}
	
	public void initTextAreas() {
		
		HorizontalLayout hLayout1 = new HorizontalLayout();
		hLayout1.setWidth("100%");
		
		Label label1 = new Label("몫: ");
		
		taQuot = new TextArea();
		taQuot.setWidth("100%");
		taQuot.addStyleName("borderless");
		
		hLayout1.addComponent(label1);
		hLayout1.addComponent(taQuot);
		hLayout1.setExpandRatio(taQuot, 1.0f);
		hLayout1.setComponentAlignment(label1, Alignment.TOP_LEFT);
		hLayout1.setComponentAlignment(taQuot, Alignment.TOP_LEFT);
		
		
		HorizontalLayout hLayout2 = new HorizontalLayout();
		hLayout2.setWidth("100%");
		
		Label label2 = new Label("나머지: ");
		
		taRemain = new TextArea();
		taRemain.setWidth("100%");
		taRemain.addStyleName("borderless");
		
		hLayout2.addComponent(label2);
		hLayout2.addComponent(taRemain);
		hLayout2.setExpandRatio(taRemain, 1.0f);
		hLayout2.setComponentAlignment(label2, Alignment.TOP_LEFT);
		hLayout2.setComponentAlignment(taRemain, Alignment.TOP_LEFT);
		
		vLayout.addComponent(hLayout1);
		vLayout.addComponent(hLayout2);
		vLayout.setExpandRatio(hLayout2, 1.0f);
	}
}
