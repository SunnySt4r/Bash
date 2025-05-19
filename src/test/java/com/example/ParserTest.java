package com.example;

import com.example.command.AssignmentCommand;
import com.example.command.Cat;
import com.example.command.Command;
import com.example.command.Echo;
import com.example.command.Exit;
import com.example.command.ExternalCommand;
import com.example.command.Grep;
import com.example.command.Ls;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ParserTest {

    @BeforeEach
    void setUp() {
        // Очистка переменных перед каждым тестом
        SessionVariables.getInstance().set("MY_VAR", null);
    }

    @Test
    void parse_SingleEchoCommand_ReturnsEchoCommand() {
        String input = "echo hello world";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Echo.class, commands.getFirst());
        Echo echo = (Echo) commands.getFirst();
        assertEquals("hello world", echo.getMessage());
    }

    @Test
    void parse_SingleExitCommand_ReturnsExitCommand() {
        String input = "exit";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Exit.class, commands.getFirst());
    }

    @Test
    void parse_SingleCatCommandWithArguments_ReturnsCatCommandWithArguments() {
        String input = "cat file1.txt file2.txt";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Cat.class, commands.getFirst());
    }

    @Test
    void parse_SingleLsCommand_ReturnsLsCommand() {
        String input = "ls";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Ls.class, commands.getFirst());
    }

    @Test
    void parse_SingleGrepCommandWithArguments_ReturnsGrepCommandWithArguments() {
        String input = "grep -i pattern file.txt";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Grep.class, commands.getFirst());
    }

    @Test
    void parse_MultipleCommandsWithPipe_ReturnsListOfCommands() {
        String input = "echo hello | grep llo | cat";
        List<Command> commands = Parser.parse(input);
        assertEquals(3, commands.size());
        assertInstanceOf(Echo.class, commands.get(0));
        assertInstanceOf(Grep.class, commands.get(1));
        assertInstanceOf(Cat.class, commands.get(2));
    }

    @Test
    void parse_UnknownCommand_ReturnsExternalCommand() {
        String input = "unknown_command arg1 arg2";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(ExternalCommand.class, commands.getFirst());
    }

    @Test
    void parse_CommandWithExtraSpaces_ParsesCorrectly() {
        String input = "  echo   hello  world   ";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Echo.class, commands.getFirst());
    }

    @Test
    void parse_PipeWithExtraSpaces_ParsesCorrectly() {
        String input = "echo hello |  grep world ";
        List<Command> commands = Parser.parse(input);
        assertEquals(2, commands.size());
        assertInstanceOf(Echo.class, commands.get(0));
        assertInstanceOf(Grep.class, commands.get(1));
    }

    @Test
    void parse_SingleAssignmentWithoutQuotes() {
        String input = "MY_VAR=hello";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(AssignmentCommand.class, commands.getFirst());
        AssignmentCommand assignmentCommand = (AssignmentCommand) commands.getFirst();
        assignmentCommand.execute();
        assertEquals("hello", SessionVariables.getInstance().get("MY_VAR"));
    }

    @Test
    void parse_SingleAssignmentWithSingleQuotes() {
        String input = "MY_VAR='hello world'";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(AssignmentCommand.class, commands.getFirst());
        AssignmentCommand assignmentCommand = (AssignmentCommand) commands.getFirst();
        assignmentCommand.execute();
        assertEquals("hello world", SessionVariables.getInstance().get("MY_VAR"));
    }

    @Test
    void parse_SingleAssignmentWithDoubleQuotes() {
        SessionVariables.getInstance().set("OTHER_VAR", "initial value");
        String input = "MY_VAR=\"value of $OTHER_VAR\"";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(AssignmentCommand.class, commands.getFirst());
        AssignmentCommand assignmentCommand = (AssignmentCommand) commands.getFirst();
        assignmentCommand.execute();
        assertEquals("value of initial value", SessionVariables.getInstance().get("MY_VAR"));
    }

    @Test
    void parse_AssignmentFollowedByCommand() {
        String input = "MY_VAR=test | echo $MY_VAR";
        List<Command> commands = Parser.parse(input);
        assertEquals(2, commands.size());
        assertInstanceOf(AssignmentCommand.class, commands.get(0));
        assertInstanceOf(Echo.class, commands.get(1));
        AssignmentCommand assignmentCommand = (AssignmentCommand) commands.get(0);
        assignmentCommand.execute();
        assertEquals("test", SessionVariables.getInstance().get("MY_VAR"));
        Echo echo = (Echo) commands.get(1);
        assertEquals("", echo.getMessage());
    }

    @Test
    void parse_CommandFollowedByAssignment() {
        String input = "echo hello | MY_VAR=world";
        List<Command> commands = Parser.parse(input);
        assertEquals(2, commands.size());
        assertInstanceOf(Echo.class, commands.get(0));
        assertInstanceOf(AssignmentCommand.class, commands.get(1));
        AssignmentCommand assignmentCommand = (AssignmentCommand) commands.get(1);
        assignmentCommand.execute();
        assertEquals("world", SessionVariables.getInstance().get("MY_VAR"));
    }

    @Test
    void parse_CommandWithArgumentsInQuotes() {
        String input = "echo 'hello world' \"another arg\"";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Echo.class, commands.getFirst());
        Echo echo = (Echo) commands.getFirst();
        assertEquals("hello world another arg", echo.getMessage());
    }

    @Test
    void parse_CommandWithVariableSubstitutionInDoubleQuotes() {
        SessionVariables.getInstance().set("SUB_VAR", "substituted");
        String input = "echo \"variable is $SUB_VAR\"";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Echo.class, commands.getFirst());
        Echo echo = (Echo) commands.getFirst();
        assertEquals("variable is substituted", echo.getMessage());
    }

    @Test
    void parse_CommandWithVariableSubstitutionNoQuotes() {
        SessionVariables.getInstance().set("SUB_VAR", "substituted");
        String input = "echo variable is $SUB_VAR";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Echo.class, commands.getFirst());
        Echo echo = (Echo) commands.getFirst();
        assertEquals("variable is substituted", echo.getMessage());
    }

    @Test
    void parse_CommandWithNoSubstitutionInSingleQuotes() {
        SessionVariables.getInstance().set("SUB_VAR", "substituted");
        String input = "echo 'variable is $SUB_VAR'";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Echo.class, commands.getFirst());
        Echo echo = (Echo) commands.getFirst();
        assertEquals("variable is $SUB_VAR", echo.getMessage());
    }

    @Test
    void parse_CommandWithEscapedPipe() {
        String input = "echo hello\\|world";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Echo.class, commands.getFirst());
        Echo echo = (Echo) commands.getFirst();
        assertEquals("hello|world", echo.getMessage());
    }

    @Test
    void parse_AssignmentWithVariableInValue() {
        SessionVariables.getInstance().set("VALUE", "actual_value");
        String input = "MY_VAR=$VALUE";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(AssignmentCommand.class, commands.getFirst());
        AssignmentCommand assignmentCommand = (AssignmentCommand) commands.getFirst();
        assignmentCommand.execute();
        assertEquals("actual_value", SessionVariables.getInstance().get("MY_VAR"));
    }

    @Test
    void parse_AssignmentWithVariableInDoubleQuotedValue() {
        SessionVariables.getInstance().set("VALUE", "quoted value");
        String input = "MY_VAR=\"prefix $VALUE suffix\"";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(AssignmentCommand.class, commands.getFirst());
        AssignmentCommand assignmentCommand = (AssignmentCommand) commands.getFirst();
        assignmentCommand.execute();
        assertEquals("prefix quoted value suffix", SessionVariables.getInstance().get("MY_VAR"));
    }

    @Test
    void parse_AssignmentWithVariableInSingleQuotedValue() {
        SessionVariables.getInstance().set("VALUE", "will not be substituted");
        String input = "MY_VAR='$VALUE'";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(AssignmentCommand.class, commands.getFirst());
        AssignmentCommand assignmentCommand = (AssignmentCommand) commands.getFirst();
        assignmentCommand.execute();
        assertEquals("$VALUE", SessionVariables.getInstance().get("MY_VAR"));
    }
}
