package com.moxel.store.cart;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CartStore {

    private static final String SESSION_KEY = "moxel.cart";

    public Cart getOrCreate(HttpSession session) {
        synchronized (WebUtils.getSessionMutex(session)) {
            Cart cart = (Cart) session.getAttribute(SESSION_KEY);
            if (cart == null) {
                cart = new Cart();
                session.setAttribute(SESSION_KEY, cart);
            }
            return cart;
        }
    }

    /** Used for the navbar badge. Never creates a session. */
    public int itemCount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return 0;
        }
        Object cart = session.getAttribute(SESSION_KEY);
        return (cart instanceof Cart c) ? c.itemCount() : 0;
    }
}