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

            if (checkWin(resp, currentSession, field)){
                return;
            }
            int emptyFieldIndex = field.getEmptyFieldIndex();
            if (emptyFieldIndex >= 0) {
                field.getField().put(emptyFieldIndex, Sign.NOUGHT);
                if (checkWin(resp, currentSession, field)){
                    return;
                }
            }
            else {
                // Добавляем в сессию флаг, который сигнализирует что произошла ничья
                currentSession.setAttribute("draw", true);

                // Считаем список значков
                List<Sign> data = field.getFieldData();

                // Обновляем этот список в сессии
                currentSession.setAttribute("data", data);

                // Шлем редирект
                resp.sendRedirect("/index.jsp");
                return;
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

    private boolean checkWin(HttpServletResponse resp, HttpSession currentSession, Field field) throws IOException{
        Sign winner = field.checkWin();
        if (winner != Sign.EMPTY){
            currentSession.setAttribute("winner", winner);
            currentSession.setAttribute("field", field);
            currentSession.setAttribute("data", field.getFieldData());
            resp.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}