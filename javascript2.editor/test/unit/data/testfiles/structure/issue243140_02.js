var myModule = (function()
{
    return {
        foo: function()
        {
            console.log("foo");
        },
        bar: "bar",
        address: {
            street: "Naskove",
            town: "Prague",
            zip: "15000"
        }
    };
})();  

var yourModule = myModule;