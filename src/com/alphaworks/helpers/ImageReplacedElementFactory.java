package com.alphaworks.helpers;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import com.alphaworks.R;
import com.lowagie.text.Image;


public class ImageReplacedElementFactory implements ReplacedElementFactory {
	
	private final ReplacedElementFactory superFactory;
	

	public ImageReplacedElementFactory(ReplacedElementFactory replacedElementFactory) {
		super();
		superFactory = replacedElementFactory;
	}

	@Override
	public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, UserAgentCallback uac, int cssWidth,
			int cssHeight) {
		Element e = box.getElement();
		if(e==null)
			return null;
		String nodeName = e.getNodeName(), className = e.getAttribute("class");
		if("div".equals(nodeName) && "media".equals(className)) {
			if(!e.hasAttribute("data-src")) {
				throw new RuntimeException("div with media classname has not data-src attribute");
			}
			
			InputStream is = null;
			try {
				is = R.loadFile(e.getAttribute("data-src"));
				final byte[] bytes = IOUtils.toByteArray(is);
				final Image image = Image.getInstance(bytes);
				final int factor = ((ITextUserAgent)uac).getSharedContext().getDotsPerPixel();
				image.scaleAbsolute(image.getPlainWidth()*factor, image.getPlainHeight()*factor);
				final FSImage fsImage = new ITextFSImage(image);
				if(fsImage != null) {
					if(cssWidth != -1 || cssHeight != -1) {
						fsImage.scale(cssWidth, cssHeight);
					}
					return new ITextImageElement(fsImage);
				}
			}
			catch(Exception _e) {
				IOUtils.closeQuietly(is);
				throw new RuntimeException("Unknown error!");
			}
		}

		return this.superFactory.createReplacedElement(c, box, uac, cssWidth, cssHeight);
	}
	

	@Override
	public void remove(Element arg0) {
		this.superFactory.remove(arg0);
	}

	@Override
	public void reset() {
		superFactory.reset();
	}

	@Override
	public void setFormSubmissionListener(FormSubmissionListener arg0) {
		superFactory.setFormSubmissionListener(arg0);
	}

	
}
