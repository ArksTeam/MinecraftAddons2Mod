import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

public class JavaScriptCompiler {
    public static String compile(String javascriptCode) {
        CompilationUnit cu = JavaScriptParser.parse(javascriptCode);
        StringBuilder javaCode = new StringBuilder();
        for (TypeDeclaration<?> type : cu.getTypes()) {
            for (BodyDeclaration<?> member : type.getMembers()) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration) member;
                    javaCode.append(methodDeclaration.getNameAsString())
                            .append("(")
                            .append(convertParameters(methodDeclaration.getParameters()))
                            .append(") {\n")
                            .append(convertStatements(methodDeclaration.getBody().getStatements()))
                            .append("\n}\n");
                } else if (member instanceof FieldDeclaration) {
                    FieldDeclaration fieldDeclaration = (FieldDeclaration) member;
                    javaCode.append(fieldDeclaration.toString())
                            .append(";\n");
                }
            }
        }

        return javaCode.toString();
    }

    private static String convertParameters(NodeList<Parameter> parameters) {
        StringBuilder params = new StringBuilder();
        for (Parameter parameter : parameters) {
            params.append(parameter.getType().asString())
                    .append(" ")
                    .append(parameter.getNameAsString())
                    .append(", ");
        }
        if (params.length() > 0) {
            params.setLength(params.length() - 2);
        }
        return params.toString();
    }

    private static String convertStatements(NodeList<Statement> statements) {
        StringBuilder stmts = new StringBuilder();
        for (Statement statement : statements) {
            stmts.append(convertStatement(statement))
                    .append("\n");
        }
        return stmts.toString();
    }

    private static String convertStatement(Statement statement) {
        if (statement instanceof ExpressionStmt) {
            return convertExpression(((ExpressionStmt) statement).getExpression()) + ";";
        } else if (statement instanceof VariableDeclarationExpr) {
            VariableDeclarationExpr variableDeclaration = (VariableDeclarationExpr) statement;
            String type = variableDeclaration.getElementType().asString();
            String name = variableDeclaration.getVariables().get(0).getNameAsString();
            return type + " " + name + ";";
        } else if (statement instanceof IfStmt) {
            IfStmt ifStatement = (IfStmt) statement;
            return "if (" + convertExpression(ifStatement.getCondition()) + ") {\n" +
                    convertStatements(ifStatement.getThenStmt().asBlockStmt().getStatements()) +
                    "} else {\n" +
                    convertStatements(ifStatement.getElseStmt().get().asBlockStmt().getStatements()) +
                    "}";
        } else if (statement instanceof BlockStmt) {
            BlockStmt blockStatement = (BlockStmt) statement;
            return "{\n" + convertStatements(blockStatement.getStatements()) + "}";
        }
        return statement.toString();
    }

    private static String convertExpression(Expression expression) {
        if (expression instanceof LiteralExpr) {
            return ((LiteralExpr) expression).asString();
        } else if (expression instanceof NameExpr) {
            return ((NameExpr) expression).getNameAsString();
        } else if (expression instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) expression;
            return convertExpression(binaryExpr.getLeft()) + " " +
                    binaryExpr.getOperator().asString() + " " +
                    convertExpression(binaryExpr.getRight());
        }
        return expression.toString();
    }
}
