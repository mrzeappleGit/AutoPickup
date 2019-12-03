private ServerMock server;
private MyPlugin plugin;

@Before
public void setUp()
{
    server = MockBukkit.mock();
    plugin = (MyPlugin) MockBukkit.load(MyPlugin.class);
}

@After
public void tearDown()
{
    MockBukkit.unload();
}