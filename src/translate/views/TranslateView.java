package translate.views;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import translate.AbstractTranslate;
import translate.impl.BaiduTranslate;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class TranslateView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "translate.views.TranslateView";

	private Text inputText;
	private Button clear;
	private Text resultText;
	private Display display;

	private ExecutorService executor;
	private AbstractTranslate translate;

	public TranslateView() {
		translate = new BaiduTranslate();
		executor = Executors.newFixedThreadPool(1);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		display = Display.getDefault();
		inputText = new Text(parent, SWT.BORDER);
		inputText.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		clear = new Button(parent, SWT.COLOR_BLUE | SWT.FLAT);
		clear.setFont(new Font(display, new FontData("", 15, SWT.NO)));
		clear.setText("clean");

		resultText = new Text(parent, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI
				| SWT.WRAP);
		resultText.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		inputTextListener();
		clearListener();
	}

	private void inputTextListener() {
		inputText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				Text text = (Text) arg0.getSource();
				String src = text.getText();
				if (src != null && !src.equals("")) {
					doTranslate(src);
				}
			}
		});
	}

	private void clearListener() {
		clear.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				inputText.setText("");
				resultText.setText("");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});
	}

	/**
	 * 异步翻译，主要是为了保证在文本框中输入内容的流畅性
	 * 
	 * @param src
	 */
	private void doTranslate(final String src) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				final String text = translate.translate(src);
				display.asyncExec(new Runnable() {

					@Override
					public void run() {
						resultText.setText(text);
					}
				});
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}
}