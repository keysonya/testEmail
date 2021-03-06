package com.epam.project.Utils;

import com.epam.project.Elements.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by Iana_Kasimova on 1/24/2018.
 */
public class CustomFieldDecorator implements FieldDecorator {

    protected ElementLocatorFactory factory;
    protected JavascriptExecutor js;

    public CustomFieldDecorator(ElementLocatorFactory factory, JavascriptExecutor js) {
        this.factory = factory;
        this.js = js;
    }

    public Object decorate(ClassLoader loader, Field field) {
        if (!BaseElement.class.isAssignableFrom(field.getType())) {
            return null;
        }

        ElementLocator locator = factory.createLocator(field);
        if (locator == null) {
            return null;
        }


        return proxyForLocator(loader, locator, field, js);
    }

    protected Object proxyForLocator(ClassLoader loader,
                                     ElementLocator locator, Field field, JavascriptExecutor js) {
        InvocationHandler handler = new LocatingElementHandler(locator);

        WebElement proxy;
        proxy = (WebElement) Proxy.newProxyInstance(
                loader, new Class[]{WebElement.class, WrapsElement.class, Locatable.class}, handler);
        if (field.getType() == Button.class) {
            return new Button(proxy, js);
        }
        if (field.getType() == TextBox.class) {
            return new TextBox(proxy, js);
        }
        if (field.getType() == Image.class) {
            return new Image(proxy, js);
        }
        if (field.getType() == CheckBox.class) {
            return new CheckBox(proxy, js);
        }
        return proxy;
    }
}
