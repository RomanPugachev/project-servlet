package com.tictactoe;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



import java.io.IOException;
import java.util.List;
import java.util.Map;


@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int index = getSelectedIndex(req);

            HttpSession currentSession = req.getSession();

            Field field = extractField(currentSession);

            if (field.getField().get(index) != Sign.EMPTY){
                getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
                return;
            }


            field.getField().put(index, Sign.CROSS);

            int emptyFieldIndex = field.getEmptyFieldIndex();

            if (emptyFieldIndex >= 0) {
                field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            }

            List<Sign> data = field.getFieldData();

            currentSession.setAttribute("field", field);
            currentSession.setAttribute("data", data);

            resp.sendRedirect("/index.jsp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }
}