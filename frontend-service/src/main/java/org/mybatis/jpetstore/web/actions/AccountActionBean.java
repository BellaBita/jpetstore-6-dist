/**
 *    Copyright (C) 2010-2017 the original author or authors.
 *                  2017 iObserve Project (https://www.iobserve-devops.net)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.jpetstore.web.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.mybatis.jpetstore.domain.Account;
import org.mybatis.jpetstore.domain.Product;
import org.mybatis.jpetstore.service.AccountService;
import org.mybatis.jpetstore.service.CatalogService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SessionScope;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

/**
 * The Class AccountActionBean.
 *
 * @author Eduardo Macarron
 */
@SessionScope
public class AccountActionBean extends AbstractActionBean {

	private static final long serialVersionUID = 5499663666155758178L;

	private final static Logger LOG = Logger.getLogger(AccountActionBean.class);

	private static final String NEW_ACCOUNT = "/WEB-INF/jsp/account/NewAccountForm.jsp";
	private static final String EDIT_ACCOUNT = "/WEB-INF/jsp/account/EditAccountForm.jsp";
	private static final String SIGNON = "/WEB-INF/jsp/account/SignonForm.jsp";

	private static final List<String> LANGUAGE_LIST;
	private static final List<String> CATEGORY_LIST;

	@SpringBean
	private transient AccountService accountService;

	@SpringBean
	private transient CatalogService catalogService;

	private Account account = new Account();
	private List<Product> myList;
	private boolean authenticated;
	private String repeatedPassword;
	
	static {
		final List<String> langList = new ArrayList<>();
		langList.add("english");
		langList.add("japanese");
		LANGUAGE_LIST = Collections.unmodifiableList(langList);

		final List<String> catList = new ArrayList<>();
		catList.add("FISH");
		catList.add("DOGS");
		catList.add("REPTILES");
		catList.add("CATS");
		catList.add("BIRDS");
		CATEGORY_LIST = Collections.unmodifiableList(catList);
	}

	public Account getAccount() {
		return this.account;
	}

	public String getUsername() {
		return this.account.getUsername();
	}

	public String getRepeatedPassword() {
		return repeatedPassword;
	}

	public void setRepeatedPassword(String repeatedPassword) {
		this.repeatedPassword = repeatedPassword;
	}

	/**
	 * Set the user name for the account.
	 *
	 * @param username name of the user
	 */
	@Validate(required = true, on = { "signon", "newAccount", "editAccount" })
	public void setUsername(final String username) {
		this.account.setUsername(username);
	}

	public String getPassword() {
		return this.account.getPassword();
	}

	/**
	 * Set the password for the account.
	 *
	 * @param password password for the account
	 */
	@Validate(required = true, on = { "signon", "newAccount", "editAccount" })
	public void setPassword(final String password) {
		this.account.setPassword(password);
	}

	public List<Product> getMyList() {
		return this.myList;
	}

	public void setMyList(final List<Product> myList) {
		this.myList = myList;
	}

	public List<String> getLanguages() {
		return AccountActionBean.LANGUAGE_LIST;
	}

	public List<String> getCategories() {
		return AccountActionBean.CATEGORY_LIST;
	}

	/**
	 * Forward to new account.
	 *
	 * @return forward resolution
	 */
	public Resolution newAccountForm() {
		return new ForwardResolution(AccountActionBean.NEW_ACCOUNT);
	}

	/**
	 * @SessionScope + @SpringBean + transient 멤버 체크
	 */
	private void checkAutowiredSpringBean() {
		if (this.catalogService == null) {
			this.catalogService = this.getSpringBean(CatalogService.class);
		}
		if (this.accountService == null) {
			this.accountService = this.getSpringBean(AccountService.class);
		}
	}

	/**
	 * New account.
	 *
	 * @return the resolution
	 * @throws IOException
	 */
	public Resolution newAccount() {
		this.checkAutowiredSpringBean();
		this.accountService.insertAccount(this.account);
		AccountActionBean.LOG.info("account " + this.account.toString());
		this.account = this.accountService.getAccount(this.account.getUsername());
		AccountActionBean.LOG.info(this.account != null ? "account" : "nope");
		this.myList = this.catalogService.getProductListByCategory(this.account.getFavouriteCategoryId());
		this.authenticated = true;
		return new RedirectResolution(CatalogActionBean.class);
	}

	/**
	 * Edits the account form.
	 *
	 * @return the resolution
	 */
	public Resolution editAccountForm() {
		return new ForwardResolution(AccountActionBean.EDIT_ACCOUNT);
	}

	/**
	 * Edits the account.
	 *
	 * @return the resolution
	 * @throws IOException
	 */
	public Resolution editAccount() {
		this.checkAutowiredSpringBean();
		this.accountService.updateAccount(this.account);
		this.account = this.accountService.getAccount(this.account.getUsername());
		this.myList = this.catalogService.getProductListByCategory(this.account.getFavouriteCategoryId());
		return new RedirectResolution(CatalogActionBean.class);
	}

	/**
	 * Signon form.
	 *
	 * @return the resolution
	 */
	@DefaultHandler
	public Resolution signonForm() {
		return new ForwardResolution(AccountActionBean.SIGNON);
	}

	/**
	 * Signon.
	 *
	 * @return the resolution
	 */
	public Resolution signon() {

		this.checkAutowiredSpringBean();
		this.account = this.accountService.getAccount(this.getUsername(), this.getPassword());

		if (this.account == null) {
			final String value = "Invalid username or password.  Signon failed.";
			this.setMessage(value);
			this.clear();
			return new ForwardResolution(AccountActionBean.SIGNON);
		} else {
			this.checkAutowiredSpringBean();
			this.account.setPassword(null);
			this.myList = this.catalogService.getProductListByCategory(this.account.getFavouriteCategoryId());
			this.authenticated = true;
			final HttpSession s = this.context.getRequest().getSession();
			// this bean is already registered as /actions/Account.action
			s.setAttribute("accountBean", this);
			return new RedirectResolution(CatalogActionBean.class);
		}
	}

	/**
	 * Signoff.
	 *
	 * @return the resolution
	 */
	public Resolution signoff() {
		this.context.getRequest().getSession().invalidate();
		this.clear();
		return new RedirectResolution(CatalogActionBean.class);
	}

	/**
	 * Checks if is authenticated.
	 *
	 * @return true, if is authenticated
	 */
	public boolean isAuthenticated() {
		return this.authenticated && (this.account != null) && (this.account.getUsername() != null);
	}

	/**
	 * Clear.
	 */
	public void clear() {
		this.account = new Account();
		this.myList = null;
		this.authenticated = false;
	}

}
