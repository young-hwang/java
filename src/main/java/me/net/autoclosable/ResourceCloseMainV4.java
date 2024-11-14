package me.net.autoclosable;

public class ResourceCloseMainV4 {
    public static void main(String[] args) {
        try {
            logic();
        } catch (CallException e) {
            System.out.println("CallException Main");
            Throwable[] suppressed = e.getSuppressed();
            for (Throwable throwable : suppressed) {
                System.out.println("suppressedEx = " + throwable);
            }
            throw new RuntimeException(e);
        } catch (CloseException e) {
            System.out.println("CloseException");
            throw new RuntimeException(e);
        }
    }

    private static void logic() throws CallException, CloseException {
        try(ResourceV2 resource1 = new ResourceV2("resource1");
          ResourceV2 resource2 = new ResourceV2("resource2")) {
            resource1.call();
            resource2.callEx();
        } catch (CallException e) {
            System.out.println("CallException: " + e);
            throw e;
        }
    }
}
