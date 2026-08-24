package tasktracker;

import java.util.Arrays;
import java.util.Scanner;
import tasktracker.cli.AddTaskCommand;
import tasktracker.cli.Command;
import tasktracker.cli.CommandRegistry;
import tasktracker.cli.CompleteTaskCommand;
import tasktracker.cli.HelpCommand;
import tasktracker.cli.ListTasksCommand;
import tasktracker.exception.TaskNotFoundException;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.repository.TaskRepository;
import tasktracker.service.TaskService;

public class App {

    public static void main(String[] args) {
        TaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);

        CommandRegistry registry = new CommandRegistry();
        registry.register(new AddTaskCommand(service));
        registry.register(new ListTasksCommand(service));
        registry.register(new CompleteTaskCommand(service));
        registry.register(new HelpCommand(registry));

        run(registry);
    }

    private static void run(CommandRegistry registry) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) {
                    break;
                }

                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] tokens = line.split("\\s+");
                String name = tokens[0];
                String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

                Command command = registry.find(name);
                if (command == null) {
                    System.out.println("Comando no reconocido: " + name);
                    continue;
                }

                try {
                    command.execute(args);
                } catch (IllegalArgumentException | TaskNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
