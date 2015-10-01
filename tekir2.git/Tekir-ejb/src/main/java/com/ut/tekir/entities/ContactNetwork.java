/*
 * Copyleft 2007-2011 Ozgur Yazilim A.S.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * www.tekir.com.tr
 * www.ozguryazilim.com.tr
 *
 */

package com.ut.tekir.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="CONTACT_NETWORK")
public class ContactNetwork extends AuditBase implements Serializable {
	
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private Contact owner;

    @Column(name="INFO")
    private String info;

    @Column(name="ACTIVE_NETWORK")
    private Boolean activeNetwork = Boolean.TRUE;
    
    @Column(name="DEFAULT_NETWORK")
    private Boolean defaultNetwork = Boolean.FALSE;

    @Column(name="TWITTER_NETWORK")
    private Boolean twitterNetwork = Boolean.FALSE;

    @Column(name="FACEBOOK_NETWORK")
    private Boolean facebookNetwork = Boolean.FALSE;

    @Column(name="MIRC_NETWORK")
    private Boolean mircNetwork = Boolean.FALSE;
    
    @Column(name="SKYPE_NETWORK")
    private Boolean skypeNetwork = Boolean.FALSE;

    @Column(name="MSN_NETWORK")
    private Boolean msnNetwork = Boolean.FALSE;

    @Column(name="GTALK_NETWORK")
    private Boolean gtalkNetwork = Boolean.FALSE;
    
    @Column(name="JABBER_NETWORK")
    private Boolean jabberNetwork = Boolean.FALSE;

    @Column(name="FFEED_NETWORK")
    private Boolean friendFeedNetwork = Boolean.FALSE;

    @Column(name="YAHOO_NETWORK")
    private Boolean yahooNetwork = Boolean.FALSE;

    @Column(name="EMAIL")
    private Boolean email = Boolean.FALSE;

    @Column(name="WEB")
    private Boolean web = Boolean.FALSE;

    @Column(name="VALUE")
    private String value;

    public String getIconName() {
    
    	if (email) return "mailNetworkIcon";
    	if (friendFeedNetwork) return "friendFeedNetworkIcon";
    	if (jabberNetwork) return "jabberNetworkIcon";
    	if (gtalkNetwork) return "gtalkNetworkIcon";
    	if (msnNetwork) return "msnNetworkIcon";
    	if (skypeNetwork) return "skypeNetworkIcon";
    	if (mircNetwork) return "mircNetworkIcon";
    	if (facebookNetwork) return "facebookNetworkIcon";
    	if (twitterNetwork) return "twitterNetworkIcon";
    	if (yahooNetwork) return "yahooNetworkIcon";
    	if (web) return "webNetworkIcon";
    	return null;
    }
    
    public void setNetwork(String network) {
    	web = Boolean.FALSE;
    	yahooNetwork = Boolean.FALSE;
    	twitterNetwork = Boolean.FALSE;
    	facebookNetwork = Boolean.FALSE;
    	mircNetwork = Boolean.FALSE;
    	skypeNetwork = Boolean.FALSE;
    	msnNetwork = Boolean.FALSE;
    	gtalkNetwork = Boolean.FALSE;
    	jabberNetwork = Boolean.FALSE;
    	friendFeedNetwork = Boolean.FALSE;
    	email = Boolean.FALSE;

    	if (network.equals("web")) {
    		web = Boolean.TRUE;
    	} else if (network.equals("yahooNetwork")) {
    		yahooNetwork = Boolean.TRUE;
    	} else if (network.equals("twitterNetwork")) {
    		twitterNetwork = Boolean.TRUE;
    	} else if (network.equals("facebookNetwork")) {
    		facebookNetwork = Boolean.TRUE;
    	} else if (network.equals("mircNetwork")) {
    		mircNetwork = Boolean.TRUE;
    	} else if (network.equals("skypeNetwork")) {
    		skypeNetwork = Boolean.TRUE;
    	} else if (network.equals("msnNetwork")) {
    		msnNetwork = Boolean.TRUE;
    	} else if (network.equals("gtalkNetwork")) {
    		gtalkNetwork = Boolean.TRUE;
    	} else if (network.equals("jabberNetwork")) {
    		jabberNetwork = Boolean.TRUE;
    	} else if (network.equals("friendFeedNetwork")) {
    		friendFeedNetwork = Boolean.TRUE;
    	} else if ( network.equals("email") ) {
    		email = Boolean.TRUE;
    	}
    }

    public String getNetwork() {
    	if (email) return "email";
    	if (friendFeedNetwork) return "friendFeedNetwork";
    	if (jabberNetwork) return "jabberNetwork";
    	if (gtalkNetwork) return "gtalkNetwork";
    	if (msnNetwork) return "msnNetwork";
    	if (skypeNetwork) return "skypeNetwork";
    	if (mircNetwork) return "mircNetwork";
    	if (facebookNetwork) return "facebookNetwork";
    	if (twitterNetwork) return "twitterNetwork";
    	if (yahooNetwork) return "yahooNetwork";
    	if (web) return "web";
    	return null;
    }

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ContactNetwork)) {
            return false;
        }
        ContactNetwork other = (ContactNetwork)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ContactNetwork[id=" + id + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Contact getOwner() {
		return owner;
	}

	public void setOwner(Contact owner) {
		this.owner = owner;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Boolean getActiveNetwork() {
		return activeNetwork;
	}

	public void setActiveNetwork(Boolean activeNetwork) {
		this.activeNetwork = activeNetwork;
	}

	public Boolean getDefaultNetwork() {
		return defaultNetwork;
	}

	public void setDefaultNetwork(Boolean defaultNetwork) {
		this.defaultNetwork = defaultNetwork;
	}

	public Boolean getTwitterNetwork() {
		return twitterNetwork;
	}

	public void setTwitterNetwork(Boolean twitterNetwork) {
		this.twitterNetwork = twitterNetwork;
	}

	public Boolean getFacebookNetwork() {
		return facebookNetwork;
	}

	public void setFacebookNetwork(Boolean facebookNetwork) {
		this.facebookNetwork = facebookNetwork;
	}

	public Boolean getMircNetwork() {
		return mircNetwork;
	}

	public void setMircNetwork(Boolean mircNetwork) {
		this.mircNetwork = mircNetwork;
	}

	public Boolean getSkypeNetwork() {
		return skypeNetwork;
	}

	public void setSkypeNetwork(Boolean skypeNetwork) {
		this.skypeNetwork = skypeNetwork;
	}

	public Boolean getEmail() {
		return email;
	}

	public void setEmail(Boolean email) {
		this.email = email;
	}

	public Boolean getWeb() {
		return web;
	}

	public void setWeb(Boolean web) {
		this.web = web;
	}

	public Boolean getMsnNetwork() {
		return msnNetwork;
	}

	public void setMsnNetwork(Boolean msnNetwork) {
		this.msnNetwork = msnNetwork;
	}

	public Boolean getGtalkNetwork() {
		return gtalkNetwork;
	}

	public void setGtalkNetwork(Boolean gtalkNetwork) {
		this.gtalkNetwork = gtalkNetwork;
	}

	public Boolean getJabberNetwork() {
		return jabberNetwork;
	}

	public void setJabberNetwork(Boolean jabberNetwork) {
		this.jabberNetwork = jabberNetwork;
	}

	public Boolean getFriendFeedNetwork() {
		return friendFeedNetwork;
	}

	public void setFriendFeedNetwork(Boolean friendFeedNetwork) {
		this.friendFeedNetwork = friendFeedNetwork;
	}

	public Boolean getYahooNetwork() {
		return yahooNetwork;
	}

	public void setYahooNetwork(Boolean yahooNetwork) {
		this.yahooNetwork = yahooNetwork;
	}

}
